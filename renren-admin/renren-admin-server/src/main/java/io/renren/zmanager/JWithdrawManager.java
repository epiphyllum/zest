package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.ledgers.LedgerCardWithdraw;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardapply.dto.TCardApplyQuery;
import io.renren.zin.cardapply.dto.TCardApplyResponse;
import io.renren.zin.cardmoney.ZinCardMoneyService;
import io.renren.zin.cardmoney.dto.TWithdrawRequest;
import io.renren.zin.cardmoney.dto.TWithdrawResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JWithdrawManager {

    @Resource
    private  JFeeConfigDao jFeeConfigDao;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ApiNotify apiNotify;
    @Resource
    private LedgerCardWithdraw ledgerCardWithdraw;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JWithdrawDao jWithdrawDao;
    @Resource
    private JCardDao jCardDao;

    // 填充信息
    public JSubEntity fillInfo(JWithdrawEntity entity) {
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
//        entity.setMaincardno(cardEntity.getMaincardno());
        return subEntity;
    }

    /**
     * 保存提现
     */
    public void save(JWithdrawEntity entity) {
        JSubEntity subEntity = fillInfo(entity);

        // 填充payerid, 什么币种的卡， 就用哪个通联va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayeeid(jVaEntity.getTid());
        entity.setState("00");

        // 产品配置
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
        if (feeConfig == null) {
            throw new RenException("没有配置");
        }
        BigDecimal merchantfee = entity.getAmount().multiply(feeConfig.getChargeRate()).setScale(2, RoundingMode.HALF_UP).negate();
        entity.setMerchantfee(merchantfee);

        try {
            tx.executeWithoutResult(st -> {
                jWithdrawDao.insert(entity);
                ledgerCardWithdraw.ledgeCardWithdrawFreeze(entity, subEntity);
            });
        } catch (Exception ex) {
            log.error("提现失败, 记录:{}, 子商户:{}", entity, subEntity);
            throw ex;
        }
    }

    /**
     * 查询
     */
    public void query(JWithdrawEntity entity, boolean notify) {
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

        // 变成成功
        if (newState.equals(ZinConstant.CARD_APPLY_SUCCESS) && !oldState.equals(ZinConstant.CARD_APPLY_SUCCESS)) {
            entity.setSecurityamount(response.getSecurityamount());
            entity.setSecuritycurrency(response.getSecuritycurrency());
            entity.setFee(response.getFee());
            entity.setFeecurrency(response.getFeecurrency());

            try {
                tx.executeWithoutResult(st -> {
                    int update = jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                            .eq(JWithdrawEntity::getId, entity.getId())
                            .eq(JWithdrawEntity::getState, oldState)
                            .set(JWithdrawEntity::getState, newState)
                            .set(JWithdrawEntity::getStateexplain, response.getStateexplain())
                            .set(JWithdrawEntity::getSecurityamount, response.getSecurityamount())
                            .set(JWithdrawEntity::getSecuritycurrency, response.getSecuritycurrency())
                            .set(JWithdrawEntity::getFee, response.getFee())
                            .set(JWithdrawEntity::getFeecurrency, response.getFeecurrency())
                    );
                    if (update != 1) {
                        throw new RenException("更新提现记录失败");
                    }
                    ledgerCardWithdraw.ledgeCardWithdraw(entity, subEntity);
                });
            } catch (Exception ex) {
                log.error("提现记账失败, 记录:{}, 子商户:{}", entity, subEntity);
                ex.printStackTrace();
                throw ex;
            }

            CompletableFuture.runAsync(() -> {
                JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, entity.getCardno()));
                jCardManager.balanceCard(cardEntity);
            });

        } else {
            jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                    .eq(JWithdrawEntity::getId, entity.getId())
                    .eq(JWithdrawEntity::getState, oldState)
                    .set(JWithdrawEntity::getState, newState)
                    .set(JWithdrawEntity::getStateexplain, response.getStateexplain())
            );
        }

        // 需要通知商户的话
        if (entity.getApi().equals(1) && notify) {
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            JWithdrawEntity freshEntity = jWithdrawDao.selectById(entity.getId());
            apiNotify.cardWithdrawNotify(freshEntity, merchant);
        }
    }

    /**
     * 提交
     */
    public void submit(JWithdrawEntity entity) {
        TWithdrawRequest request = ConvertUtils.sourceToTarget(entity, TWithdrawRequest.class);
        TWithdrawResponse response = zinCardMoneyService.withdraw(request);
        JWithdrawEntity update = new JWithdrawEntity();
        update.setId(entity.getId());
        update.setApplyid(response.getApplyid());
        jWithdrawDao.updateById(update);

        // 立即查询通联
        entity.setApplyid(response.getApplyid());
        this.query(entity, false);
    }

    /**
     * 作废
     *
     * @param entity
     */
    public void cancel(JWithdrawEntity entity) {
        JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
        try {
            tx.executeWithoutResult(st -> {
                jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                        .eq(JWithdrawEntity::getId, entity.getId())
                        .isNull(JWithdrawEntity::getApplyid)
                        .set(JWithdrawEntity::getState, ZinConstant.PAY_APPLY_FAIL)
                );
                ledgerCardWithdraw.ledgeCardWithdrawUnFreeze(entity, subEntity);
            });
        } catch (Exception ex) {
            log.error("提现作废失败, 记录:{}, 子商户:{}", entity, subEntity);
            ex.printStackTrace();
            throw ex;
        }
    }
}
