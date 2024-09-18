package io.renren.zin.service.va;

import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.va.dto.TMoneyInNotify;
import io.renren.zin.service.va.dto.TVaListRequest;
import io.renren.zin.service.va.dto.TVaListResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
@Slf4j
public class ZinVaService {
    @Resource
    private ZinRequester requester;
    @Resource
    private MerchantMoneyInHandler merchantMoneyInHandler;

    // 查询通联账户列表: 6004
    public TVaListResponse vaList() {
        String requestId = newRequestId();
        TVaListRequest tVaListRequest = new TVaListRequest();
        TVaListResponse response = requester.request(requestId, "/gcpapi/va/search", tVaListRequest, TVaListResponse.class);
        return response;
    }

    // VA入金信息查询: todo

    // 历史余额查询: todo

    // 账务明细查询: todo

    // 账户余额查询: todo

    // 入账通知
    public void moneyInNotify(TMoneyInNotify notify) {
        merchantMoneyInHandler.handle(notify);
    }
}
