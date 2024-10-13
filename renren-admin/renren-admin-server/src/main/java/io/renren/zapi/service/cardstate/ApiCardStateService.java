package io.renren.zapi.service.cardstate;

import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.cardmoney.ApiCardMoneyService;
import io.renren.zapi.service.cardstate.dto.CardConstant;
import io.renren.zapi.service.cardstate.dto.*;
import io.renren.zin.service.TResult;
import io.renren.zin.service.cardapply.ZinCardApplyService;
import io.renren.zin.service.cardapply.dto.TCardPayInfoRequest;
import io.renren.zin.service.cardapply.dto.TCardPayInfoResponse;
import io.renren.zin.service.cardmoney.ZinCardMoneyService;
import io.renren.zin.service.cardmoney.dto.TCardBalanceRequest;
import io.renren.zin.service.cardmoney.dto.TCardBalanceResponse;
import io.renren.zin.service.cardstate.ZinCardStateService;
import io.renren.zin.service.cardstate.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class ApiCardStateService {
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStateService zinCardStateService;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;

    // 卡状态变更申请
    public Result cardChange(CardChangeReq request, ApiCardMoneyService context) {

        // 准备应答
        TResult tResult = null;

        switch (request.getChangetype()) {
            case CardConstant.CARD_LOSS:
                TCardLossRequest tCardLossRequest = ConvertUtils.sourceToTarget(request, TCardLossRequest.class);
                tResult = zinCardStateService.cardLoss(tCardLossRequest);
                break;
            case CardConstant.CARD_UNLOSS:
                TCardUnlossRequest tCardUnlossRequest = ConvertUtils.sourceToTarget(request, TCardUnlossRequest.class);
                tResult = zinCardStateService.cardUnloss(tCardUnlossRequest);
                break;
            case CardConstant.CARD_CANCEL:
                TCardCancelRequest tCardCancelRequest = ConvertUtils.sourceToTarget(request, TCardCancelRequest.class);
                tResult = zinCardStateService.cardCancel(tCardCancelRequest);
                break;
            case CardConstant.CARD_UNCANCEL:
                TCardUncancelRequest tCardUncancelRequest = ConvertUtils.sourceToTarget(request, TCardUncancelRequest.class);
                tResult = zinCardStateService.cardUncancel(tCardUncancelRequest);
                break;
            case CardConstant.CARD_FREEZE:
                TCardFreezeRequest tCardFreezeRequest = ConvertUtils.sourceToTarget(request, TCardFreezeRequest.class);
                tResult = zinCardStateService.cardFreeze(tCardFreezeRequest);
                break;
            case CardConstant.CARD_UNFREEZE:
                TCardUnfreezeRequest tCardUnfreezeRequest = ConvertUtils.sourceToTarget(request, TCardUnfreezeRequest.class);
                tResult = zinCardStateService.cardUnfreeze(tCardUnfreezeRequest);
        }
        if (tResult == null) {
            throw new RenException("internal error");
        }

        if (tResult.getRspcode().equals("0000")) {
            return new Result();
        }
        return Result.fail(9999, tResult.getRspinfo());
    }

    // 卡余额查询
    public Result<CardBalanceRes> cardBalance(CardBalanceReq request, ApiContext context) {
        TCardBalanceRequest tCardBalanceRequest = ConvertUtils.sourceToTarget(request, TCardBalanceRequest.class);

        TCardBalanceResponse balance = zinCardMoneyService.balance(tCardBalanceRequest);
        CardBalanceRes cardBalanceRes = ConvertUtils.sourceToTarget(balance, CardBalanceRes.class);

        Result<CardBalanceRes> result = new Result<>();
        result.setData(cardBalanceRes);
        return result;
    }

    // 卡支付信息: cvv2 | expiredate | 不外提供
    public Result<CardPayInfoRes> cardPayInfo(CardPayInfoReq request, ApiContext context) {
        TCardPayInfoRequest tCardPayInfoRequest = ConvertUtils.sourceToTarget(request, TCardPayInfoRequest.class);
        TCardPayInfoResponse tCardPayInfoResponse = zinCardApplyService.cardPayInfo(tCardPayInfoRequest);
        CardPayInfoRes cardPayInfoRes = ConvertUtils.sourceToTarget(tCardPayInfoResponse, CardPayInfoRes.class);

        Result<CardPayInfoRes> result = new Result<>();
        result.setData(cardPayInfoRes);
        return result;
    }
}
