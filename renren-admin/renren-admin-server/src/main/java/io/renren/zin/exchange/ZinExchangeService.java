package io.renren.zin.exchange;

import io.renren.zin.ZinRequester;
import io.renren.zin.exchange.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static io.renren.zcommon.CommonUtils.uniqueId;

@Service
public class ZinExchangeService {
    @Resource
    private ZinRequester requester;

    // 发起换汇: 2000
    public TExchangeResponse exchange(TExchangeRequest tExchangeRequest) {
        return requester.request(uniqueId(), "/gcpapi/card/exchangeoff/apply", tExchangeRequest, TExchangeResponse.class);
    }

    // 锁汇申请: 2001
    public TExchangeLockResponse exchangeLock(TExchangeLockRequest tExchangeLockRequest) {
        return requester.request(uniqueId(), "/gcpapi/apply/lockfx", tExchangeLockRequest, TExchangeLockResponse.class);
    }

    // 换汇确认: 2002
    public TExchangeConfirmResponse exchangeConfirm(TExchangeConfirmRequest tExchangeConfirmRequest) {
        return requester.request(uniqueId(), "/gcpapi/b2bwithdraw/confirm", tExchangeConfirmRequest, TExchangeConfirmResponse.class);
    }

    // 换汇查询: 2003
    public TExchangeQueryResponse exchangeQuery(TExchangeQueryRequest tExchangeQueryRequest) {
        return requester.request(uniqueId(), "/gcpapi/apply/query", tExchangeQueryRequest, TExchangeQueryResponse.class);
    }

    // 换汇查询: 2005
    public TExchangeRateResponse exchangeRate(TExchangeRateRequest request) {
        return requester.request(uniqueId(), "/gcpapi/card/getexrate", request, TExchangeRateResponse.class);
    }
}
