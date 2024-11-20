package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.allocate.dto.MoneyNotify;
import io.renren.zbalance.ledgers.LedgerMoneyIn;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZapiConstant;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.accountmanage.ZinAccountManageNotifyService;
import io.renren.zin.accountmanage.dto.TMoneyInNotify;
import io.renren.zin.file.ZinFileService;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JMoneyManager {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ZinAccountManageNotifyService zinAccountManageNotifyService;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private LedgerMoneyIn ledgerMoneyIn;
    @Resource
    private ApiNotifyService apiNotifyService;
    @Resource
    private JMerchantDao jMerchantDao;

    // 保存
    public void saveAndSubmit(JMoneyEntity entity, JMerchantEntity merchant, String cardno) {
        // 填充商户代理信息
        entity.setMerchantName(merchant.getCusname());
        entity.setAgentName(merchant.getAgentName());
        entity.setAgentId(merchant.getAgentId());

        // 初始状态
        entity.setState(ZinConstant.PAY_APPLY_NEW_DJ);

        // 填充来账账户信息
        JMaccountEntity jMaccountEntity = jMaccountDao.selectOne(Wrappers.<JMaccountEntity>lambdaQuery().eq(JMaccountEntity::getCardno, cardno));
        if (jMaccountEntity == null) {
            log.error("找不到来账账户: {}", cardno);
            throw new RenException("账户不存在");
        }
        entity.setCardname(jMaccountEntity.getCardname());
        entity.setCardno(jMaccountEntity.getCardno());

        // 调用通联
        TMoneyApply apply = new TMoneyApply();
        apply.setCurrency(entity.getCurrency());
        apply.setId(jMaccountEntity.getCardid());
        apply.setMeraplid(CommonUtils.uniqueId());
        TMoneyApplyResponse response = zinUmbrellaService.depositApply(apply);
        entity.setReferencecode(response.getReferencecode());
        entity.setApplyid(response.getApplyid());
        jMoneyDao.insert(entity);
    }

    public void uploadFiles(JMoneyEntity jMoneyEntity) {
        // 拿到所有文件fid
        String transferfid = jMoneyEntity.getTransferfid();
        String otherfid = jMoneyEntity.getOtherfid();

        List<String> fids = List.of(transferfid, otherfid);
        Map<String, CompletableFuture<String>> jobs = new HashMap<>();
        for (String fid : fids) {
            if (StringUtils.isBlank(fid)) {
                continue;
            }
            jobs.put(fid, CompletableFuture.supplyAsync(() -> {
                return zinFileService.upload(fid);
            }));
        }
        jobs.forEach((j, f) -> {
            log.info("wait {}...", j);
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RenException("can not upload file:" + j);
            }
        });
    }

    // confirm
    public void confirm(JMoneyEntity entity) {

        // 提交通联
        TMoneyConfirm confirm = new TMoneyConfirm();
        confirm.setApplyid(entity.getApplyid());
        confirm.setAmount(entity.getApplyAmount());

        confirm.setOtherfid(entity.getOtherfid());
        confirm.setTransferfid(entity.getTransferfid());

        TMoneyConfirmResponse response = zinUmbrellaService.depositConfirm(confirm);
        // 更新
        JMoneyEntity updateEntity = new JMoneyEntity();
        updateEntity.setId(entity.getId());
        updateEntity.setTransferfid(entity.getTransferfid());
        updateEntity.setOtherfid(entity.getOtherfid());
        updateEntity.setApplyAmount(entity.getApplyAmount());
        updateEntity.setState(ZinConstant.PAY_APPLY_CF_DJ);
        jMoneyDao.updateById(updateEntity);
    }

    public void mockMoneyInNotify(JMoneyEntity entity) {
        TMoneyInNotify notify = new TMoneyInNotify();
        notify.setAmount(entity.getApplyAmount());
        notify.setApplyid(entity.getApplyid());
        notify.setPs(entity.getReferencecode());
        notify.setTrxcod(ZinConstant.CP213);
        notify.setNid(CommonUtils.uniqueId());
        notify.setBid(CommonUtils.uniqueId());
        notify.setPayeraccountno(entity.getCardno());
        notify.setPayeraccountbank("未知银行");
        notify.setPayeraccountcountry("CHN");
        notify.setPayeraccountname("未知名称");
        log.info("模拟通联入金通知: {}", notify);
        zinAccountManageNotifyService.handle(notify);
        if (entity.getApi().equals(1)) {
            entity = jMoneyDao.selectById(entity.getId());
            this.notifyMerchant(entity);
        }
    }

    private void notifyMerchant(JMoneyEntity entity) {
        log.info("接口商户, 通知商户...");
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        MoneyNotify moneyNotify = ConvertUtils.sourceToTarget(entity, MoneyNotify.class);
        apiNotifyService.notifyMerchant(moneyNotify, merchant, ZapiConstant.API_moneyNotify);
    }

    // 补充材料
    public void makeup(JMoneyEntity entity) {
        // 提交通联
        TMaterialSubmit submit = new TMaterialSubmit();
        submit.setApplyid(entity.getApplyid());
        submit.setOtherfid(entity.getOtherfid());
        submit.setTransferfid(entity.getTransferfid());
        TMaterialSubmitResponse response = zinUmbrellaService.submitMaterial(submit);

        // 更新
        JMoneyEntity updateEntity = new JMoneyEntity();
        updateEntity.setId(entity.getId());
        updateEntity.setTransferfid(entity.getTransferfid());
        updateEntity.setOtherfid(entity.getOtherfid());
        updateEntity.setApplyAmount(entity.getApplyAmount());
        updateEntity.setState(ZinConstant.PAY_APPLY_CF_DJ);
        jMoneyDao.updateById(updateEntity);
    }

    // 人工匹配
    public void matchMoney(Long id) {
        JMoneyEntity moneyEntity = jMoneyDao.selectById(id);
        String payeraccountno = moneyEntity.getPayeraccountno();
        JMaccountEntity jMaccountEntity = jMaccountDao.selectOne(Wrappers.<JMaccountEntity>lambdaQuery()
                .eq(JMaccountEntity::getCardno, payeraccountno)
        );

        // 匹配来账账号
        if (jMaccountEntity == null) {
            throw new RenException("来账账户不匹配");
        }

        moneyEntity.setMerchantId(jMaccountEntity.getMerchantId());
        moneyEntity.setMerchantName(jMaccountEntity.getMerchantName());

        // 记账
        try {
            tx.executeWithoutResult(status -> {
                int update = jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                        .eq(JMoneyEntity::getId, moneyEntity.getId())
                        .ne(JMoneyEntity::getState, ZinConstant.PAY_APPLY_LG_DJ)
                        .set(JMoneyEntity::getState, ZinConstant.PAY_APPLY_LG_DJ)
                        .set(JMoneyEntity::getAgentId, jMaccountEntity.getAgentId())
                        .set(JMoneyEntity::getAgentName, jMaccountEntity.getAgentName())
                        .set(JMoneyEntity::getMerchantId, jMaccountEntity.getMerchantId())
                        .set(JMoneyEntity::getMerchantName, jMaccountEntity.getMerchantName())
                );
                if (update != 1) {
                    throw new RenException("匹配失败");
                }
                ledgerMoneyIn.ledgeMoneyIn(moneyEntity);
            });
        } catch (Exception ex) {
            log.error("记账失败: {}", moneyEntity);
            ex.printStackTrace();
            throw ex;
        }

        // 接口商户
        if (moneyEntity.getApi().equals(1)) {
            JMoneyEntity entity = jMoneyDao.selectById(moneyEntity.getId());
            this.notifyMerchant(entity);
        }
    }
}
