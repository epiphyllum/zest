package io.renren.manager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.dto.JDepositDTO;
import io.renren.zadmin.entity.*;
import io.renren.zadmin.service.JDepositService;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZinConstant;
import io.renren.zin.service.cardapply.ZinCardApplyService;
import io.renren.zin.service.cardapply.dto.TCardApplyQuery;
import io.renren.zin.service.cardapply.dto.TCardApplyResponse;
import io.renren.zin.service.cardmoney.ZinCardMoneyService;
import io.renren.zin.service.cardmoney.dto.TDepositRequest;
import io.renren.zin.service.cardmoney.dto.TDepositResponse;
import io.renren.zin.service.file.ZinFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JDepositManager {
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private Ledger ledger;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JDepositDao jDepositDao;
    @Resource
    private JDepositService jDepositService;
    @Resource
    private JConfigDao jConfigDao;

    // 填充信息
    public JSubEntity fillInfo(JDepositEntity entity) {
        JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
        if (subEntity == null) {
            throw new RenException("in valid request, lack subId");
        }
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());

        return subEntity;
    }

    public BigDecimal calcTxnAmount(BigDecimal amount) {
        JConfigEntity jConfigEntity = jConfigDao.selectOne(Wrappers.emptyWrapper());
        if (jConfigEntity == null) {
            throw new RenException("请配置全局参数");
        }
        // 计算发起金额
        BigDecimal rate1 = BigDecimal.ONE.subtract(jConfigEntity.getChargeRate());
        BigDecimal rate2 = BigDecimal.ONE.subtract(jConfigEntity.getDepositRate());
        BigDecimal mul = rate1.multiply(rate2);
        return amount.divide(mul, 2, RoundingMode.HALF_UP);
    }

    /**
     * 保存充值
     * 这种情况下，设置充值手续费1%，担保金2%，充值金额100 HKD
     * 则：担保金=100*0.02=2 HKD
     * 充值手续费=（100-2）*0.01=0.98 HKD
     * 到账金额 = 100-2-0.98=97.02 HKD
     * x *(1-
     */
    public void save(JDepositEntity entity) {
        // 填充payerid, 什么币种的卡， 就用哪个通联va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayerid(jVaEntity.getTid());
        this.uploadFiles(entity);

        BigDecimal txnAmount = calcTxnAmount(entity.getAmount());
        entity.setTxnAmount(txnAmount);

        // 填充其他ID
        JSubEntity subEntity = fillInfo(entity);

        // 入库
        tx.executeWithoutResult(st -> {
            jDepositDao.insert(entity);
            ledger.ledgeCardChargeFreeze(entity, subEntity);
        });

        try {
            this.submit(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 上传所需文件
     */
    public void uploadFiles(JDepositEntity entity) {
        // 拿到所有文件fid
        String agmfid = entity.getAgmfid();

        List<String> fids = List.of(agmfid);
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
        log.info("文件上传完毕, 开始请求创建商户...");
    }

    /**
     * 查询充值申请单状态
     */
    public void query(final JDepositEntity entity) {
        TCardApplyQuery query = new TCardApplyQuery();
        query.setApplyid(entity.getApplyid());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);
        String oldState = entity.getState() == null ? "" : entity.getState();
        String newState = response.getState();
        // 状态无变化
        if (oldState.equals(newState)) {
            return;
        }

        JSubEntity subEntity = jSubDao.selectById(entity.getSubId());

        log.info("卡充值, state: {} -> {}", oldState, newState);
        // 变成成功
        if (newState.equals(ZinConstant.CARD_APPLY_SUCCESS) && !oldState.equals(ZinConstant.CARD_APPLY_SUCCESS)) {
            tx.executeWithoutResult(st -> {
                jDepositDao.update(null, Wrappers.<JDepositEntity>lambdaUpdate()
                        .eq(JDepositEntity::getId, entity.getId())
                        .eq(JDepositEntity::getState, oldState)
                        .set(JDepositEntity::getSecurityamount, response.getSecurityamount())
                        .set(JDepositEntity::getFee, response.getFee())
                        .set(JDepositEntity::getState, newState)
                );
                ledger.ledgeCardCharge(entity, subEntity);
            });
        } else {
            jDepositDao.update(null, Wrappers.<JDepositEntity>lambdaUpdate()
                    .eq(JDepositEntity::getId, entity.getId())
                    .eq(JDepositEntity::getState, oldState)
                    .set(JDepositEntity::getSecurityamount, response.getSecurityamount())
                    .set(JDepositEntity::getState, newState)
            );
        }
    }

    /**
     * 提交充值到通联
     */
    public void submit(JDepositEntity entity) {
        TDepositRequest request = ConvertUtils.sourceToTarget(entity, TDepositRequest.class);
        request.setAmount(entity.getTxnAmount());
        TDepositResponse response = zinCardMoneyService.deposit(request);
        String applyid = response.getApplyid();
        JDepositEntity update = new JDepositEntity();
        update.setId(entity.getId());
        update.setApplyid(applyid);
        jDepositDao.updateById(update);

        // 立即发起查询
        entity.setApplyid(applyid);
        this.query(entity);
    }

    public void update(JDepositDTO dto) {
        JDepositEntity entity = jDepositDao.selectById(dto.getId());

        log.info("oldAmount: {}, newAmount: {}", entity.getAmount(), dto.getAmount());

        // 如果修改了金额: 需要重新计算发起金额
        if (entity.getAmount().compareTo(dto.getAmount()) != 0) {
            BigDecimal txnAmount = calcTxnAmount(dto.getAmount());
            dto.setTxnAmount(txnAmount);
        }

        jDepositService.update(dto);
    }
}
