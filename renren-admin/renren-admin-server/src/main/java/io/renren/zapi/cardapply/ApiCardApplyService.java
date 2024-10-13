package io.renren.zapi.cardapply;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.cardapply.dto.*;

import io.renren.zin.cardapply.dto.TCardApplyQuery;
import io.renren.zin.cardapply.dto.TCardApplyResponse;
import io.renren.zin.cardapply.dto.TCardSubApplyRequest;
import io.renren.zin.cardapply.dto.TCardSubApplyResponse;
import io.renren.zin.cardstate.ZinCardStateService;
import io.renren.zin.cardstate.dto.TCardActivateRequest;
import io.renren.zin.cardstate.dto.TCardActivateResponse;
import io.renren.zin.cardapply.ZinCardApplyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class ApiCardApplyService {

    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStateService zinCardStateService;

    // 开卡
    public Result<CardNewRes> cardNew(CardNewReq request, ApiContext context) {
        TCardSubApplyRequest tCardSubApplyRequest = ConvertUtils.sourceToTarget(request, TCardSubApplyRequest.class);
        TCardSubApplyResponse tCardSubApplyResponse = zinCardApplyService.cardSubApply(tCardSubApplyRequest);
        CardNewRes cardNewRes = ConvertUtils.sourceToTarget(tCardSubApplyResponse, CardNewRes.class);

        Result<CardNewRes> result = new Result<>();
        result.setData(cardNewRes);
        return result;
    }

    // 开卡查询
    public Result<CardNewQueryRes> cardNewQuery(CardNewQuery request, ApiContext context) {
        TCardApplyQuery tCardApplyQuery = ConvertUtils.sourceToTarget(request, TCardApplyQuery.class);
        TCardApplyResponse tCardApplyResponse = zinCardApplyService.cardApplyQuery(tCardApplyQuery);
        CardNewQueryRes cardNewQueryRes = ConvertUtils.sourceToTarget(tCardApplyResponse, CardNewQueryRes.class);

        Result<CardNewQueryRes> result = new Result<>();
        result.setData(cardNewQueryRes);
        return result;
    }

    // 卡激活
    public Result<CardNewActivateRes> cardNewActivate(CardNewActivateReq request, ApiContext context) {
        TCardActivateRequest tCardActivateRequest = ConvertUtils.sourceToTarget(request, TCardActivateRequest.class);
        TCardActivateResponse tCardActivateResponse = zinCardStateService.cardActivate(tCardActivateRequest);
        CardNewActivateRes cardNewActivateRes = ConvertUtils.sourceToTarget(tCardActivateResponse, CardNewActivateRes.class);

        Result<CardNewActivateRes> result = new Result<>();
        result.setData(cardNewActivateRes);
        return result;
    }
}
