package io.renren.zin.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zapi.ApiNotifyService;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JExchangeManager;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiService;
import io.renren.zapi.exchange.dto.ExchangeNotify;
import io.renren.zin.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// CP110 离岸换汇通知
@Service
@Slf4j
public class CP110 {
    @Resource
    private JExchangeDao jExchangeDao;
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
        if (jExchangeEntity.getState().equals(ZinConstant.PAY_APPLY_FIRST_VERIFY)) {
            return;
        }
        // 查询下通联: 同时通知下下游商户
        jExchangeManager.query(jExchangeEntity, true);
    }
}
