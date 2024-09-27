package io.renren.zapi.service.allocate;


import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.allocate.dto.*;
import io.renren.zbalance.Ledger;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAllocateService {

    @Resource
    private ApiService apiService;
    @Resource
    private Ledger ledger;

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
            entity.setFromId(null);
            entity.setToId(null);
            entity.setFromName(null);
            entity.setToName(null);
            ledger.ledgeI2v(merchant, entity);
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
            entity.setFromId(null);
            entity.setToId(null);
            entity.setFromName(null);
            entity.setToName(null);
            ledger.ledgeV2i(merchant, entity);
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
            entity.setFromId(null);
            entity.setToId(null);
            entity.setFromName(null);
            entity.setToName(null);
            ledger.ledgeM2s(merchant, entity);
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
            entity.setFromId(null);
            entity.setToId(null);
            entity.setFromName(null);
            entity.setToName(null);
            ledger.ledgeS2m(merchant, entity);
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
        apiService.notifyMerchant(notify, merchant, "notify");
    }

}
