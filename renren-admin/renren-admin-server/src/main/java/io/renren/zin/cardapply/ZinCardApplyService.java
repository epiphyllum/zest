package io.renren.zin.cardapply;

import io.renren.zin.ZinRequester;
import io.renren.zin.cardapply.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.renren.zcommon.CommonUtils.newRequestId;

@Service
@Slf4j
public class ZinCardApplyService {
    @Resource
    private ZinRequester requester;

    // 主卡申请: 3000
    public TCardMainApplyResponse cardMainApply(TCardMainApplyRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/open", request, TCardMainApplyResponse.class);
    }

    // 子卡申请: 3001
    public TCardSubApplyResponse cardSubApply(TCardSubApplyRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/subopen", request, TCardSubApplyResponse.class);
    }

    // 新增VPA场景: 3002
    public TCardAddSceneResponse cardAddScene(TCardAddScene request) {
        return requester.request(newRequestId(), "/gcpapi/card/addcardscene", request, TCardAddSceneResponse.class);
    }

    // VPA场景查询: 3003 todo
    public TCardQuerySceneResponse cardQueryScene(TCardQueryScene request) {
        return requester.request(newRequestId(), "/gcpapi/card/qrycardscene", request, TCardQuerySceneResponse.class);
    }

    // 申请VPA子卡: 3004 todo
    public TCardVpaApplyResponse cardVpaApply(TCardVpaApply request) {
        return requester.request(newRequestId(), "/gcpapi/card/vpaopen", request, TCardVpaApplyResponse.class);
    }


    // 卡申请单查询:  3006
    // 1. 主卡/子卡申请
    // 2. 缴纳保证金
    // 3. 提取保证金
    // 4. 卡注销申请单当前处理进度。
    public TCardApplyResponse cardApplyQuery(TCardApplyQuery query) {
        return requester.request(newRequestId(), "/gcpapi/card/applyquery", query, TCardApplyResponse.class);
    }

    // 查询卡支付信息: 3007
    public TCardPayInfoResponse cardPayInfo(TCardPayInfoRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/getpayinfo", request, TCardPayInfoResponse.class);
    }

    // 变更VPA子卡场景: 3008 todo
    public TCardUpdateSceneResponse cardQueryScene(TCardUpdateScene request) {
        return requester.request(newRequestId(), "/gcpapi/card/cardupdatescene", request, TCardUpdateSceneResponse.class);
    }
}
