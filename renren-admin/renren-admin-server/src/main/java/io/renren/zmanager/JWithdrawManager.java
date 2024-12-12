package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.LedgerUtil;
import io.renren.zbalance.ledgers.Ledger700CardWithdraw;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JWithdrawManager {

    @Resource
    private JCommon jCommon;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ApiNotify apiNotify;
    @Resource
    private Ledger700CardWithdraw ledger700CardWithdraw;
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
    @Resource
    private LedgerUtil ledgerUtil;

    // 填充信息
    public JSubEntity fillInfo(JWithdrawEntity entity) {
        JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
        if (subEntity == null) {
            throw new RenException("数据非法, 缺少子商户ID");
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

    /**
     * 保存提现
     */
    public void save(JWithdrawEntity entity) {

        JSubEntity subEntity = fillInfo(entity);

        // 如果是预付费主卡提现, 那么如果有在提现的就不能提
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            Long processing = jWithdrawDao.selectCount(Wrappers.<JWithdrawEntity>lambdaQuery()
                    .eq(JWithdrawEntity::getCardno, entity.getCardno())
                    .notIn(JWithdrawEntity::getState, ZinConstant.cardStateFinal)
            );
            if (processing > 0) {
                throw new RenException("有正在进行的提现, 请完成后再操作!");
            }

            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, entity.getCardno()));
            JBalanceEntity prepaidQuotaAccount = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            if (prepaidQuotaAccount == null) {
                throw new RenException("预付费发卡额度账户不存在");
            }
            if (prepaidQuotaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
                throw new RenException("提现金额不能大于可发卡额!");
            }
        }

        // 填充payerid, 什么币种的卡， 就用哪个通联va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayeeid(jVaEntity.getTid());
        entity.setState("00");

        // 卡产品配置
        JFeeConfigEntity feeConfig = jCommon.getFeeConfig(entity.getMerchantId(), entity.getMarketproduct(), entity.getCurrency());

        // 计算商户手续费
        BigDecimal merchantfee = entity.getAmount().multiply(feeConfig.getChargeRate()).setScale(2, RoundingMode.HALF_UP).negate();
        entity.setMerchantfee(merchantfee);

        try {
            tx.executeWithoutResult(st -> {
                jWithdrawDao.insert(entity);
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

            Date statDate = new Date();

            try {
                Boolean execute = tx.execute(st -> {
                    int update = jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                            .eq(JWithdrawEntity::getId, entity.getId())
                            .eq(JWithdrawEntity::getState, oldState)
                            .set(JWithdrawEntity::getState, newState)
                            .set(JWithdrawEntity::getStateexplain, response.getStateexplain())
                            .set(JWithdrawEntity::getSecurityamount, response.getSecurityamount())
                            .set(JWithdrawEntity::getSecuritycurrency, response.getSecuritycurrency())
                            .set(JWithdrawEntity::getFee, response.getFee())
                            .set(JWithdrawEntity::getFeecurrency, response.getFeecurrency())
                            .set(JWithdrawEntity::getStatDate, statDate)
                    );
                    if (update != 1) {
                        st.setRollbackOnly();
                        return false;
                    }
                    ledger700CardWithdraw.ledgeCardWithdraw(entity, subEntity);
                    return true;
                });
                if (execute) {
                    CompletableFuture.runAsync(() -> {
                        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, entity.getCardno()));
                        jCardManager.balanceCard(cardEntity);
                    });
                }
            } catch (Exception ex) {
                log.error("提现记账失败, 记录:{}, 子商户:{}", entity, subEntity);
                ex.printStackTrace();
                throw ex;
            }
        } else {
            jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                    .eq(JWithdrawEntity::getId, entity.getId())
                    .eq(JWithdrawEntity::getState, oldState)
                    .set(JWithdrawEntity::getState, newState)
                    .set(JWithdrawEntity::getStateexplain, response.getStateexplain())
            );
        }

        // 需要通知商户的话
        if (notify || entity.getApi().equals(1)) {
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
            });
        } catch (Exception ex) {
            log.error("提现作废失败, 记录:{}, 子商户:{}", entity, subEntity);
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * 通知商户
     * @param id
     */
    public void notify(Long id) {
        JWithdrawEntity withdrawEntity = jWithdrawDao.selectById(id);
        Long merchantId = withdrawEntity.getMerchantId();
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
        apiNotify.cardWithdrawNotify(withdrawEntity, merchant);
    }
}
