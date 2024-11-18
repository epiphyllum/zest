package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.ledgers.LedgerExchange;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.exchange.ZinExchangeService;
import io.renren.zin.exchange.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class JExchangeManager {

    @Resource
    private ZinExchangeService zinExchangeService;
    @Resource
    private JExchangeDao jExchangeDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private LedgerExchange ledgerExchange;
    @Resource
    private ApiNotify apiNotify;

    /**
     * 保存
     */
    public void save(JExchangeEntity entity) {
        entity.setState(ZinConstant.PAY_APPLY_NEW_DJ);  // 内部状态新建
        tx.executeWithoutResult(status -> {
            jExchangeDao.insert(entity);
            ledgerExchange.ledgeExchangeFreeze(entity);
        });
    }

    /**
     * 提交到通联
     */
    public void submit(JExchangeEntity entity) {
        TExchangeRequest request = ConvertUtils.sourceToTarget(entity, TExchangeRequest.class);
        TExchangeResponse exchange = zinExchangeService.exchange(request);
        jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                .eq(JExchangeEntity::getId, entity.getId())
                .set(JExchangeEntity::getApplyid, exchange.getApplyid())
        );
        entity.setApplyid(exchange.getApplyid());
        this.query(entity, false);
    }

    /**
     * 查询通联
     */
    public void query(JExchangeEntity jExchangeEntity, boolean notify) {

        // 调用通联
        TExchangeQueryRequest query = new TExchangeQueryRequest();
        query.setApplyid(jExchangeEntity.getApplyid());
        TExchangeQueryResponse response = zinExchangeService.exchangeQuery(query);

        // 准备状态更新
        JExchangeEntity update = ConvertUtils.sourceToTarget(response, JExchangeEntity.class);
        update.setExfee(response.getFee());
        update.setExfxrate(response.getFxrate());
        update.setId(jExchangeEntity.getId());

        String oldState = jExchangeEntity.getState();
        String newState = response.getState();
        // 状态从不成功到成功
        if (ZinConstant.payApplyStateMap.get(oldState) != ZinConstant.STATE_SUCCESS &&
                ZinConstant.payApplyStateMap.get(newState) == ZinConstant.STATE_SUCCESS) {
            tx.executeWithoutResult(st -> {
                int cnt = jExchangeDao.update(update, Wrappers.<JExchangeEntity>lambdaUpdate()
                        .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                        .ne(JExchangeEntity::getState, ZinConstant.PAY_APPLY_SUCCESS)
                );
                if (cnt != 1) {
                    throw new RenException("更新换汇状态失败");
                }
                jExchangeEntity.setExfee(response.getFee());
                jExchangeEntity.setExfxrate(response.getFxrate());
                ledgerExchange.ledgeExchange(jExchangeEntity);
            });
        } else {
            // 其他情况,  只是简单更新
            jExchangeDao.updateById(update);
        }

        // 是否通知商户
        if (notify) {
            JExchangeEntity entity = jExchangeDao.selectById(jExchangeEntity.getId());
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            apiNotify.exchangeNotify(entity, merchant);
        }
    }

    /**
     * 锁汇
     */
    public JExchangeEntity lock(JExchangeEntity jExchangeEntity) {
        TExchangeLockRequest request = new TExchangeLockRequest(jExchangeEntity.getApplyid(), null);
        request.setApplyid(jExchangeEntity.getApplyid());
        TExchangeLockResponse response = zinExchangeService.exchangeLock(request);
        JExchangeEntity update = ConvertUtils.sourceToTarget(response, JExchangeEntity.class);
        update.setExtype("LK");
        update.setId(jExchangeEntity.getId());
        jExchangeDao.updateById(update);
        return jExchangeDao.selectById(jExchangeEntity.getId());
    }

    /**
     * 换汇确认
     */
    public void confirm(JExchangeEntity jExchangeEntity) {
        TExchangeConfirmRequest request = new TExchangeConfirmRequest();
        request.setApplyid(jExchangeEntity.getApplyid());
        request.setExtype(jExchangeEntity.getExtype());
        TExchangeConfirmResponse response = zinExchangeService.exchangeConfirm(request);
        JExchangeEntity update = new JExchangeEntity();
        update.setId(jExchangeEntity.getId());
        update.setState(ZinConstant.PAY_APPLY_CF_DJ);  // 中间内部态: 以提交
        jExchangeDao.updateById(update);
        // 查询通联
        this.query(jExchangeEntity, false);
    }

    /**
     * 取消换汇
     */
    public void cancel(JExchangeEntity entity) {
        tx.executeWithoutResult(status -> {
            int update = jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                    .eq(JExchangeEntity::getId, entity.getId())
                    .set(JExchangeEntity::getState, ZinConstant.PAY_APPLY_CC_DJ)   // 内部取消
            );
            if (update != 1) {
                throw new RenException("取消换汇失败");
            }
            ledgerExchange.ledgeExchangeUnFreeze(entity);
        });
    }


}
