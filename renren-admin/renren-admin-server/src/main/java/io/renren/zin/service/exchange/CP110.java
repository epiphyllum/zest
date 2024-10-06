package io.renren.zin.service.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.exchange.dto.ExchangeStateNotify;
import io.renren.zbalance.Ledger;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


// CP110  离岸换汇
@Service
@Slf4j
public class CP110 {

    @Resource
    private ApiService apiService;
    @Resource
    private JExchangeDao jExchangeDao;
    @Resource
    private Ledger ledger;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private TransactionTemplate tx;

    public void handle(TExchangeStateNotify notify, int status) {
        JExchangeEntity jExchangeEntity = jExchangeDao.selectOne(Wrappers.<JExchangeEntity>lambdaQuery()
                .eq(JExchangeEntity::getApplyid, notify.getApplyid())
        );
        if (jExchangeEntity == null) {
            log.error("can not find exchange with notify: {}", notify.getApplyid());
            throw new RenException("invalid meraplid");
        }

        // 已经是终态了: 只需要通知商户
        if (jExchangeEntity.getState().equals("06")) {
            log.info("换汇已经是终态！");
            notifyMerchant(notify, jExchangeEntity);
            return;
        }

        // 只有换汇成功， 且之前不是成功
        if (status == 1 && !jExchangeEntity.getState().equals("06")) {
            log.info("通知换汇成功, 且之前是不成功");
            tx.executeWithoutResult(st -> {
                jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                        .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                        .ne(JExchangeEntity::getState, "06")
                        .set(JExchangeEntity::getState, "06")
                        .set(JExchangeEntity::getExfxrate, notify.getFxrate())
                        .set(JExchangeEntity::getExfee, notify.getFee())
                );
                jExchangeEntity.setExfxrate(notify.getFxrate());
                jExchangeEntity.setExfxrate(notify.getFee());
                ledger.ledgeExchange(jExchangeEntity);
            });
        } else {
            // 准备待更新内容
            JExchangeEntity updateEntity = ConvertUtils.sourceToTarget(notify, JExchangeEntity.class);
            updateEntity.setId(jExchangeEntity.getId());
            updateEntity.setExfxrate(notify.getFxrate());
            updateEntity.setExfee(notify.getFee());
            jExchangeDao.updateById(updateEntity);
        }
        notifyMerchant(notify, jExchangeEntity);
    }

    private void notifyMerchant(TExchangeStateNotify notify, JExchangeEntity jExchangeEntity) {
        // 通知商户
        JMerchantEntity merchant = jMerchantDao.selectById(jExchangeEntity.getMerchantId());
        ExchangeStateNotify exchangeStateNotify = ConvertUtils.sourceToTarget(notify, ExchangeStateNotify.class);
        exchangeStateNotify.setMeraplid(jExchangeEntity.getMeraplid());  // 换下meraplid
        String ok = apiService.notifyMerchant(exchangeStateNotify, merchant, "exchangeNotify");
        if (!ok.equals("OK")) {
            throw new RenException("process not completed");
        }

    }
}
