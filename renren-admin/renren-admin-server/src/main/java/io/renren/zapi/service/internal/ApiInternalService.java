package io.renren.zapi.service.internal;

import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.internal.dto.*;
import io.renren.zin.service.exchange.ZinExchangeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiInternalService {
    @Resource
    private ApiService apiService;

    // 入金账户查询
    public Result<InMoneyAccountRes> inMoneyAccount(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        InMoneyAccountReq request = apiService.<InMoneyAccountReq>initRequest(InMoneyAccountReq.class, context, merchantId, reqId, name, sign, body);
        Result<InMoneyAccountRes> result = new Result<>();
        return result;
    }

    // va账户查询
    public Result<VaAccountRes> vaAccount(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        VaAccountReq request = apiService.<VaAccountReq>initRequest(VaAccountReq.class, context, merchantId, reqId, name, sign, body);
        Result<VaAccountRes> result = new Result<>();
        return result;
    }

    // 子商户va查询
    public Result<VaSubAccountRes> vaSubAccount(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        VaSubAccountReq request = apiService.<VaSubAccountReq>initRequest(VaSubAccountReq.class, context, merchantId, reqId, name, sign, body);
        Result<VaSubAccountRes> result = new Result<>();
        return result;
    }
}
