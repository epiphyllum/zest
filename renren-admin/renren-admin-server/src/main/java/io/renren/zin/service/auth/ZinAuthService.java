package io.renren.zin.service.auth;

import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.auth.dto.TAuthSettledQuery;
import io.renren.zin.service.auth.dto.TAuthSettledResponse;
import io.renren.zin.service.auth.dto.TAuthTxnNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
public class ZinAuthService {

    @Resource
    private ZinRequester requester;

    // 4000-授权交易明细查询: todo

    // 4001-已入账交易明细查询
    public TAuthSettledResponse settledQuery(TAuthSettledQuery query) {
        return requester.request(newRequestId(), "/gcpapi/card/open", query, TAuthSettledResponse.class);
    }

    // 4002-授权交易通知
    public void authTxnNotify(TAuthTxnNotify notify) {
    }

}
