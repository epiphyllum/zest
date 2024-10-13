package io.renren.zin.service.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.manager.JExchangeManager;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.exchange.dto.ExchangeNotify;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


// CP110 离岸换汇通知
@Service
@Slf4j
public class CP110 {

    @Resource
    private ApiService apiService;
    @Resource
    private JExchangeDao jExchangeDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JExchangeManager jExchangeManager;

    public void handle(TExchangeStateNotify notify) {
        JExchangeEntity jExchangeEntity = jExchangeDao.selectOne(Wrappers.<JExchangeEntity>lambdaQuery()
                .eq(JExchangeEntity::getApplyid, notify.getApplyid())
        );
        if (jExchangeEntity == null) {
            log.error("can not find exchange with notify: {}", notify.getApplyid());
            throw new RenException("invalid meraplid");
        }

        // 已经是终态了， 属于重复通知
        if (jExchangeEntity.getState().equals("06")) {
            return;
        }
        jExchangeManager.query(jExchangeEntity);
    }

    private void notifyMerchant(TExchangeStateNotify notify, JExchangeEntity jExchangeEntity) {
        // 通知商户
        JMerchantEntity merchant = jMerchantDao.selectById(jExchangeEntity.getMerchantId());
        ExchangeNotify exchangeNotify = ConvertUtils.sourceToTarget(notify, ExchangeNotify.class);
        exchangeNotify.setMeraplid(jExchangeEntity.getMeraplid());  // 换下meraplid
        String ok = apiService.notifyMerchant(exchangeNotify, merchant, "exchangeNotify");
        if (!ok.equals("OK")) {
            throw new RenException("process not completed");
        }
    }
}
