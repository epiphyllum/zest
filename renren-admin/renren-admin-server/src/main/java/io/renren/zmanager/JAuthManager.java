package io.renren.zmanager;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotify;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class JAuthManager {
    @Resource
    private JAuthDao jAuthDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ApiNotify apiNotify;

    /**
     * 通知商户
     */
    public void notify(Long id) {
        JAuthEntity jAuthEntity = jAuthDao.selectById(id);
        Long merchantId = jAuthEntity.getMerchantId();
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
        if (StringUtils.isBlank(merchant.getWebhook())) {
            throw new RenException("非接口交易, 无法通知");
        }
        apiNotify.cardTxnNotify(jAuthEntity, merchant);
    }

}
