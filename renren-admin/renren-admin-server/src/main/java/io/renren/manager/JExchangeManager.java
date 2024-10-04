package io.renren.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zin.service.exchange.ZinExchangeService;
import io.renren.zin.service.exchange.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JExchangeManager {


    @Resource
    private ZinExchangeService zinExchangeService;

    @Resource
    private JExchangeDao jExchangeDao;


    // 提交到通联
    public void submit(JExchangeEntity entity) {
        TExchangeRequest request = ConvertUtils.sourceToTarget(entity, TExchangeRequest.class);
        TExchangeResponse exchange = zinExchangeService.exchange(request);
        jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                .eq(JExchangeEntity::getId, entity.getId())
                .set(JExchangeEntity::getApplyid, exchange.getApplyid())
        );
    }

    // 查询通联
    public void query(JExchangeEntity jExchangeEntity) {
        TExchangeQueryRequest query = new TExchangeQueryRequest();
        query.setApplyid(jExchangeEntity.getApplyid());
        TExchangeQueryResponse response = zinExchangeService.exchangeQuery(query);

        JExchangeEntity update = ConvertUtils.sourceToTarget(response, JExchangeEntity.class);
        update.setExfee(response.getFee());
        update.setExfxrate(response.getFxrate());
        update.setId(jExchangeEntity.getId());
        jExchangeDao.updateById(update);
    }

    // 锁汇
    public void lock(JExchangeEntity jExchangeEntity) {
        TExchangeLockRequest request = new TExchangeLockRequest();
        request.setApplyid(jExchangeEntity.getApplyid());
        TExchangeLockResponse response = zinExchangeService.exchangeLock(request);
        JExchangeEntity update = ConvertUtils.sourceToTarget(response, JExchangeEntity.class);
        update.setId(jExchangeEntity.getId());
        jExchangeDao.updateById(update);
    }

    // 确认执行换汇确认
    public void confirm(JExchangeEntity jExchangeEntity) {
        TExchangeConfirmRequest request = new TExchangeConfirmRequest();
        request.setApplyid(jExchangeEntity.getApplyid());
        request.setExtype(jExchangeEntity.getExtype());
        TExchangeConfirmResponse response = zinExchangeService.exchangeConfirm(request);
    }
}
