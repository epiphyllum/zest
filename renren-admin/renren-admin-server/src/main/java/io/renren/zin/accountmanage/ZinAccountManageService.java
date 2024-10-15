package io.renren.zin.accountmanage;

import io.renren.zin.ZinRequester;
import io.renren.zin.accountmanage.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.renren.zcommon.CommonUtils.uniqueId;

@Service
@Slf4j
public class ZinAccountManageService {
    @Resource
    private ZinRequester requester;
    @Resource
    private ZinAccountManageNotifyService accountManageNotify;

    // 查询通联账户列表: 6004
    public TVaListResponse vaList(TVaListRequest request) {
        String requestId = uniqueId();
        TVaListResponse response = requester.request(requestId, "/gcpapi/va/search", request, TVaListResponse.class);
        return response;
    }

    // 历史余额查询: 6000: todo: not used
    public THistoryBalanceResponse historyBalance(THistoryBalanceQuery query) {
        String requestId = uniqueId();
        THistoryBalanceResponse response = requester.request(requestId, "/gcpapi/acctinfo/histbal", query, THistoryBalanceResponse.class);
        return response;
    }

    // 账务明细查询: 6001: todo: not used
    public TBalanceDetailResponse balanceDetail(TBalanceDetailQuery query) {
        String requestId = uniqueId();
        TBalanceDetailResponse response = requester.request(requestId, "/gcpapi/acctinfo/acctsdtl", query, TBalanceDetailResponse.class);
        return response;
    }

    // 账户余额查询: 6002: todo: not used
    public TBalanceResponse balance(TBalanceQuery query) {
        String requestId = uniqueId();
        TBalanceResponse response = requester.request(requestId, "/gcpapi/acctinfo/getacctbal", query, TBalanceResponse.class);
        return response;
    }


}
