package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.accountmanage.ZinAccountManageNotifyService;
import io.renren.zin.accountmanage.dto.TMoneyInNotify;
import io.renren.zin.file.ZinFileService;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    // 保存
    public void saveAndSubmit(JMoneyEntity entity, JMerchantEntity merchant, String cardId) {
        // 填充商户代理信息
        entity.setMerchantName(merchant.getCusname());
        entity.setAgentName(merchant.getAgentName());
        entity.setAgentId(merchant.getAgentId());

        // 初始状态
        entity.setState(ZinConstant.MONEY_IN_NEW);

        // 填充来账账户信息
        JMaccountEntity jMaccountEntity = jMaccountDao.selectOne(Wrappers.<JMaccountEntity>lambdaQuery().eq(JMaccountEntity::getCardid, cardId));
        if (jMaccountEntity == null) {
            log.error("找不到来账账户: {}", cardId);
            throw new RenException("账户不存在");
        }
        entity.setCardname(jMaccountEntity.getCardname());
        entity.setCardno(jMaccountEntity.getCardno());

        // 调用通联
        TMoneyApply apply = new TMoneyApply();
        apply.setCurrency(entity.getCurrency());
        apply.setId(cardId);
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
        updateEntity.setState(ZinConstant.MONEY_IN_CONFIRMED);
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

    }

}
