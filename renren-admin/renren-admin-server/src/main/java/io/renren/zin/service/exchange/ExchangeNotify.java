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
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ExchangeNotify {
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

    // 换汇通知
    public void exchangeStateNotify(TExchangeStateNotify notify) {
        JExchangeEntity jExchangeEntity = jExchangeDao.selectById(Long.parseLong(notify.getMeraplid()));
        if (jExchangeEntity == null) {
            throw new RenException("invalid meraplid");
        }

        // 已经是终态了: 只需要通知商户
        if (jExchangeEntity.getState().equals("06") || jExchangeEntity.getState().equals("07")) {
            notifyMerchant(notify, jExchangeEntity);
            return;
        }

        // 准备待更新内容
        JExchangeEntity updateEntity = ConvertUtils.sourceToTarget(notify, JExchangeEntity.class);
        updateEntity.setMeraplid(null);
        updateEntity.setId(jExchangeEntity.getId());

        // 只有换汇成功， 且换汇成功
        if (notify.getState().equals("06") && !jExchangeEntity.getState().equals("06")) {
            tx.executeWithoutResult(status -> {
                jExchangeDao.update(updateEntity, Wrappers.<JExchangeEntity>lambdaUpdate()
                        .set(JExchangeEntity::getState, "06")
                        .ne(JExchangeEntity::getState, "06")
                        .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                );
                ledger.ledgeExchange(jExchangeEntity);
            });
        } else {
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
