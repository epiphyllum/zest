package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.entity.JVaEntity;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zbalance.Ledger;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardapply.dto.TCardApplyQuery;
import io.renren.zin.cardapply.dto.TCardApplyResponse;
import io.renren.zin.cardmoney.ZinCardMoneyService;
import io.renren.zin.cardmoney.dto.TWithdrawRequest;
import io.renren.zin.cardmoney.dto.TWithdrawResponse;
import io.renren.zin.file.ZinFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@Slf4j
public class JWithdrawManager {

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private Ledger ledger;
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
        return subEntity;
    }

    /**
     * 保存充值
     */
    public void save(JWithdrawEntity entity) {
        JSubEntity subEntity = fillInfo(entity);

        // 填充payerid, 什么币种的卡， 就用哪个通联va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayeeid(jVaEntity.getTid());

        tx.executeWithoutResult(st -> {
            jWithdrawDao.insert(entity);
            ledger.ledgeCardWithdrawFreeze(entity, subEntity);
        });

//        // 立即发起提交到通联
//        try {
//            this.submit(entity);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

    }

    /**
     * 查询
     */
    public void query(JWithdrawEntity entity) {
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
            tx.executeWithoutResult(st -> {
                jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                        .eq(JWithdrawEntity::getId, entity.getId())
                        .eq(JWithdrawEntity::getState, oldState)
                        .set(JWithdrawEntity::getState, newState)
                        .set(JWithdrawEntity::getStateexplain, response.getStateexplain())
                        .set(JWithdrawEntity::getSecurityamount, response.getSecurityamount())
                );
                ledger.ledgeCardWithdraw(entity, subEntity);
            });
        } else {
            jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                    .eq(JWithdrawEntity::getId, entity.getId())
                    .eq(JWithdrawEntity::getState, oldState)
                    .set(JWithdrawEntity::getState, newState)
                    .set(JWithdrawEntity::getStateexplain, response.getStateexplain())
                    .set(JWithdrawEntity::getSecurityamount, response.getSecurityamount())
            );
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
        this.query(entity);
    }

    /**
     * 作废
     * @param entity
     */
    public void cancel(JWithdrawEntity entity) {
        JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
        tx.executeWithoutResult(st -> {
            jWithdrawDao.update(null, Wrappers.<JWithdrawEntity>lambdaUpdate()
                    .eq(JWithdrawEntity::getId, entity.getId())
                    .isNull(JWithdrawEntity::getApplyid)
                    .set(JWithdrawEntity::getState, "07")
            );
            ledger.ledgeCardWithdrawUnFreeze(entity,subEntity);
        });
    }
}
