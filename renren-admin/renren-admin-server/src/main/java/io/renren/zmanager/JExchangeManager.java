package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.Ledger;
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
    private TransactionTemplate tx;
    @Resource
    private Ledger ledger;
    @Resource
    private ApiNotify apiNotify;

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
        this.query(entity);
    }

    /**
     * 查询通联
     */
    public void query(JExchangeEntity jExchangeEntity) {

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
                jExchangeDao.update(update, Wrappers.<JExchangeEntity>lambdaUpdate()
                        .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                        .ne(JExchangeEntity::getState, "06")
                );
                jExchangeEntity.setExfee(response.getFee());
                jExchangeEntity.setExfxrate(response.getFxrate());
                ledger.ledgeExchange(jExchangeEntity);
            });
        } else {
            // 其他情况,  只是简单更新
            jExchangeDao.updateById(update);
        }

        // 是否通知商户
        if (jExchangeEntity.getApi() == 1) {
            apiNotify.exchangeNotify();
        }
    }

    /**
     * 锁汇
     */
    public void lock(JExchangeEntity jExchangeEntity) {
        TExchangeLockRequest request = new TExchangeLockRequest();
        request.setApplyid(jExchangeEntity.getApplyid());
        TExchangeLockResponse response = zinExchangeService.exchangeLock(request);
        JExchangeEntity update = ConvertUtils.sourceToTarget(response, JExchangeEntity.class);
        update.setExtype("LK");
        update.setId(jExchangeEntity.getId());
        jExchangeDao.updateById(update);
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
        update.setState(ZinConstant.PAY_APPLY_CF);  // 中间内部态: 以提交
        jExchangeDao.updateById(update);
        this.query(jExchangeEntity);
    }

    /**
     * 取消换汇
     */
    public void cancel(JExchangeEntity entity) {
        tx.executeWithoutResult(status -> {
            jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                    .eq(JExchangeEntity::getId, entity.getId())
                    .set(JExchangeEntity::getState, ZinConstant.PAY_APPLY_CC)   // 内部取消
            );
            ledger.ledgeExchangeUnFreeze(entity);
        });
    }

    /**
     * 保存
     */
    public void save(JExchangeEntity entity) {
        entity.setState(ZinConstant.PAY_APPLY_NA);  // 内部状态新建
        tx.executeWithoutResult(status -> {
            jExchangeDao.insert(entity);
            ledger.ledgeExchangeFreeze(entity);
        });
    }
}
