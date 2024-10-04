package io.renren.zapi.service.allocate;


import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.ZapiConstant;
import io.renren.zapi.notifyevent.VaDepositNotifyEvent;
import io.renren.zapi.service.allocate.dto.*;
import io.renren.zbalance.Ledger;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ApiAllocateService {

    @Resource
    private ApiService apiService;
    @Resource
    private Ledger ledger;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JMerchantDao jMerchantDao;

    // 商户入金转入va
    public Result<I2vRes> i2v(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        I2vReq request = apiService.<I2vReq>initRequest(I2vReq.class, context, merchantId, reqId, name, body, sign);

        // 记账
        {
            JMerchantEntity merchant = context.getMerchant();
            JAllocateEntity entity = new JAllocateEntity();
            entity.setMerchantId(merchantId);
            entity.setMerchantName(merchant.getCusname());
            ledger.ledgeI2v(entity, merchant);
        }

        Result<I2vRes> result = new Result<>();
        return result;
    }

    // va转商户入金
    public Result<I2vRes> v2i(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        I2vReq request = apiService.<I2vReq>initRequest(I2vReq.class, context, merchantId, reqId, name, body, sign);

        // 记账
        {
            JMerchantEntity merchant = context.getMerchant();
            JAllocateEntity entity = new JAllocateEntity();
            entity.setMerchantId(merchantId);
            entity.setMerchantName(merchant.getCusname());
            ledger.ledgeV2i(entity, merchant);
        }

        Result<I2vRes> result = new Result<>();
        return result;
    }

    // 商户转入子商户
    public Result<M2sRes> m2s(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        M2sReq request = apiService.<M2sReq>initRequest(M2sReq.class, context, merchantId, reqId, name, body, sign);

        // 记账
        {
            JMerchantEntity merchant = context.getMerchant();
            JAllocateEntity entity = new JAllocateEntity();
            entity.setMerchantId(merchantId);
            entity.setMerchantName(merchant.getCusname());
            ledger.ledgeM2s(entity);
        }

        Result<M2sRes> result = new Result<>();
        return result;
    }

    // 商户转入子商户查询
    public Result<M2sQueryRes> m2sQuery(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        M2sQuery request = apiService.<M2sQuery>initRequest(M2sQuery.class, context, merchantId, reqId, name, body, sign);

        Result<M2sQueryRes> result = new Result<>();
        return result;
    }

    // 子商户转商户
    public Result<S2mRes> s2m(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        S2mReq request = apiService.<S2mReq>initRequest(S2mReq.class, context, merchantId, reqId, name, body, sign);

        {
            JMerchantEntity merchant = context.getMerchant();
            JAllocateEntity entity = new JAllocateEntity();
            entity.setMerchantId(merchantId);
            entity.setMerchantName(merchant.getCusname());
            ledger.ledgeS2m(entity);
        }

        Result<S2mRes> result = new Result<>();
        return result;
    }

    // 子商户转商户查询
    public Result<S2mQueryRes> s2mQuery(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        S2mQuery request = apiService.<S2mQuery>initRequest(S2mQuery.class, context, merchantId, reqId, name, body, sign);
        Result<S2mQueryRes> result = new Result<>();
        return result;
    }

    // 通知入账
    public void notifyMoneyIn(MoneyInNotify notify, JMerchantEntity merchant) {
        apiService.notifyMerchant(notify, merchant, ZapiConstant.API_MONEY_IN_NOTIFY);
    }

    // 事件通知
    @EventListener
    public void handle(VaDepositNotifyEvent event) {
        Long moneyId = event.getMoneyId();
        JMoneyEntity jMoneyEntity = jMoneyDao.selectById(moneyId);
        JMerchantEntity merchant = jMerchantDao.selectById(jMoneyEntity.getMerchantId());
        MoneyInNotify moneyInNotify = ConvertUtils.sourceToTarget(jMoneyEntity, MoneyInNotify.class);
        this.notifyMoneyIn(moneyInNotify, merchant);
    }
}
