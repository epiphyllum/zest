package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.dto.JDepositDTO;
import io.renren.zadmin.entity.*;
import io.renren.zadmin.service.JDepositService;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.ledgers.LedgerCardCharge;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardapply.dto.TCardApplyQuery;
import io.renren.zin.cardapply.dto.TCardApplyResponse;
import io.renren.zin.cardmoney.ZinCardMoneyService;
import io.renren.zin.cardmoney.dto.TDepositRequest;
import io.renren.zin.cardmoney.dto.TDepositResponse;
import io.renren.zin.file.ZinFileService;
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
    private LedgerCardCharge ledgerCardCharge;
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
    private ApiNotify apiNotify;
    @Resource
    private JDepositDao jDepositDao;
    @Resource
    private JDepositService jDepositService;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JFeeConfigDao jFeeConfigDao;

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

        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, entity.getCardno())
        );
        entity.setMarketproduct(cardEntity.getMarketproduct());

        return subEntity;
    }

    private BigDecimal calcOut(BigDecimal out, JFeeConfigEntity feeConfig) {
        // 正向计算
        BigDecimal deposit = out.multiply(feeConfig.getCostDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal left = out.subtract(deposit);
        BigDecimal charge = left.multiply(feeConfig.getCostChargeRate()).setScale(2, RoundingMode.HALF_UP);
        left = left.subtract(charge);
        return left;
    }

    /**
     * 计算需要发起金额
     * 充值手续费1%，担保金2%，充值金额100 HKD,  则：
     * 担保金=100*0.02=2 HKD
     * 充值手续费=（100-2）*0.01=0.98 HKD
     * 到账金额 = 100-2-0.98=97.02 HKD
     */
    public BigDecimal calcTxnAmount(BigDecimal amount, JFeeConfigEntity feeConfig) {
        BigDecimal rate1 = BigDecimal.ONE.subtract(feeConfig.getCostChargeRate());
        BigDecimal rate2 = BigDecimal.ONE.subtract(feeConfig.getCostDepositRate());
        BigDecimal middle = amount.divide(rate1, 2, RoundingMode.HALF_UP);
        BigDecimal out = middle.divide(rate2, 2, RoundingMode.HALF_UP);

        BigDecimal left = calcOut(out, feeConfig);
        int compare = left.compareTo(amount);
        if (compare == 0) {
            return out;
        } else if (compare < 0) {
            out = out.add(new BigDecimal("0.01"));
        } else {
            out = out.subtract(new BigDecimal("0.01"));
        }

        left = calcOut(out, feeConfig);
        compare = left.compareTo(amount);
        if (compare == 0) {
            return out;
        } else {
            throw new RenException("无法反算发起金额");
        }
    }

    //  发起金额 到账金额 填充:
    public void fixAmount(JDepositEntity entity, JFeeConfigEntity feeConfig) {
        BigDecimal txnAmount = calcTxnAmount(entity.getAmount(), feeConfig);
        entity.setTxnAmount(txnAmount);

        // 成本与收入扣率
        entity.setDepositRate(feeConfig.getDepositRate());
        entity.setChargeRate(feeConfig.getChargeRate());
        entity.setCostDepositRate(feeConfig.getDepositRate());
        entity.setCostChargeRate(feeConfig.getChargeRate());

        BigDecimal merchantDeposit = entity.getAmount().multiply(feeConfig.getDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal merchantCharge = entity.getAmount().multiply(feeConfig.getChargeRate()).setScale(2, RoundingMode.HALF_UP);
        entity.setMerchantCharge(merchantCharge);
        entity.setMerchantDeposit(merchantDeposit);
    }

    public JFeeConfigEntity getFeeConfig(JDepositEntity entity) {
        // 卡产品
        JFeeConfigEntity feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                .eq(JFeeConfigEntity::getMerchantId, entity.getMerchantId())
                .eq(JFeeConfigEntity::getMarketproduct, entity.getMarketproduct())
        );
        if (feeConfig == null) {
            feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                    .eq(JFeeConfigEntity::getMerchantId, 0L)
                    .eq(JFeeConfigEntity::getMarketproduct, entity.getMarketproduct())
            );
        }
        return feeConfig;
    }

    public void saveAndSubmit(JDepositEntity entity, boolean submit) {
        // 填充payerid, 什么币种的卡， 就用哪个通联va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayerid(jVaEntity.getTid());

        // 填充id信息
        JSubEntity subEntity = fillInfo(entity);

        // 金额计算
        JFeeConfigEntity feeConfig = getFeeConfig(entity);
        fixAmount(entity, feeConfig);

        // 初始太
        entity.setState("00");

        // 入库
        try {
            tx.executeWithoutResult(st -> {
                log.info("insert deposit: {}", entity);
                jDepositDao.insert(entity);
                ledgerCardCharge.ledgeCardChargeFreeze(entity, subEntity);
            });
        } catch ( Exception ex) {
            log.error("充值记账失败, 充值记录:{}, 子商户:{}", entity, subEntity);
            ex.printStackTrace();
            throw ex;
        }

        if (submit) {
            try {
                this.submit(entity);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
    public void query(final JDepositEntity entity, boolean notify) {
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

            entity.setSecurityamount(response.getSecurityamount());
            entity.setSecuritycurrency(response.getSecuritycurrency());
            entity.setFee(response.getFee());
            entity.setFeecurrency(response.getFeecurrency());
            try {
                tx.executeWithoutResult(st -> {
                    int update = jDepositDao.update(null, Wrappers.<JDepositEntity>lambdaUpdate()
                            .eq(JDepositEntity::getId, entity.getId())
                            .eq(JDepositEntity::getState, oldState)
                            .set(JDepositEntity::getSecurityamount, response.getSecurityamount())
                            .set(JDepositEntity::getSecuritycurrency, response.getSecuritycurrency())
                            .set(JDepositEntity::getFee, response.getFee())
                            .set(JDepositEntity::getFeecurrency, response.getFeecurrency())
                            .set(JDepositEntity::getState, newState)
                    );
                    if (update != 1) {
                        throw new RenException("更新充值失败");
                    }
                    ledgerCardCharge.ledgeCardCharge(entity, subEntity);
                });
            } catch (Exception ex) {
                log.error("充值记账错误, 充值记录:{}", entity, subEntity);
                ex.printStackTrace();
                throw ex;
            }

            CompletableFuture.runAsync(() -> {
                JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, entity.getCardno()));
                jCardManager.balanceCard(cardEntity);
            });

        } else {
            jDepositDao.update(null, Wrappers.<JDepositEntity>lambdaUpdate()
                    .eq(JDepositEntity::getId, entity.getId())
                    .eq(JDepositEntity::getState, oldState)
                    .set(JDepositEntity::getState, newState)
            );
        }

        if (entity.getApi().equals(1) && notify) {
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            JDepositEntity freshEntity = jDepositDao.selectById(entity.getId());
            apiNotify.cardChargeNotify(freshEntity, merchant);
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
        this.query(entity, false);
    }

    /**
     * 修改了金额， 需要重新设置发起金额
     *
     * @param dto
     */
    public void update(JDepositDTO dto) {
        JDepositEntity entity = jDepositDao.selectById(dto.getId());
        log.info("oldAmount: {}, newAmount: {}", entity.getAmount(), dto.getAmount());
        // 如果修改了金额: 需要重新计算发起金额
        if (entity.getAmount().compareTo(dto.getAmount()) != 0) {
            JFeeConfigEntity feeConfig = getFeeConfig(entity);
            fixAmount(entity, feeConfig);
        }
        jDepositService.update(dto);
    }

    /**
     * 作废, 需要解冻
     *
     * @param entity
     */
    public void cancel(JDepositEntity entity) {
        JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
        // 入库
        try {
            tx.executeWithoutResult(st -> {
                jDepositDao.update(null, Wrappers.<JDepositEntity>lambdaUpdate()
                        .isNull(JDepositEntity::getApplyid)
                        .eq(JDepositEntity::getId, entity.getId())
                        .set(JDepositEntity::getState, "07")
                );
                ledgerCardCharge.ledgeCardChargeUnFreeze(entity, subEntity);
            });
        } catch (Exception ex) {
            log.error("充值作废失败, 记录:{}, 子商户:{}", entity, subEntity);
            ex.printStackTrace();
            throw ex;
        }
    }
}
