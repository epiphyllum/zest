package io.renren.zapi.service.card;

import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.card.dto.*;
import io.renren.zin.service.TResult;
import io.renren.zin.service.cardapply.ZinCardApplyService;
import io.renren.zin.service.cardapply.dto.*;
import io.renren.zin.service.cardstatus.ZinCardStatusService;
import io.renren.zin.service.cardstatus.dto.*;
import io.renren.zin.service.cardmoney.ZinCardMoneyService;
import io.renren.zin.service.cardmoney.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCardService {

    @Resource
    private ApiService apiService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStatusService zinCardStatusService;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;

    // 开卡
    public Result<NewCardRes> newCard(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        NewCardReq request = apiService.<NewCardReq>initRequest(NewCardReq.class, context, merchantId, reqId, name, body, sign);

        TCardSubApplyRequest tCardSubApplyRequest = ConvertUtils.sourceToTarget(request, TCardSubApplyRequest.class);
        TCardSubApplyResponse tCardSubApplyResponse = zinCardApplyService.cardSubApply(tCardSubApplyRequest);
        NewCardRes newCardRes = ConvertUtils.sourceToTarget(tCardSubApplyResponse, NewCardRes.class);

        Result<NewCardRes> result = new Result<>();
        result.setData(newCardRes);
        return result;
    }

    // 开卡查询
    public Result<CardApplyQueryRes> cardQuery(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardApplyQuery request = apiService.<CardApplyQuery>initRequest(CardApplyQuery.class, context, merchantId, reqId, name, body, sign);

        TCardApplyQuery tCardApplyQuery = ConvertUtils.sourceToTarget(request, TCardApplyQuery.class);
        TCardApplyResponse tCardApplyResponse = zinCardApplyService.cardApplyQuery(tCardApplyQuery);
        CardApplyQueryRes cardApplyQueryRes = ConvertUtils.sourceToTarget(tCardApplyResponse, CardApplyQueryRes.class);

        Result<CardApplyQueryRes> result = new Result<>();
        result.setData(cardApplyQueryRes);
        return result;
    }

    // 开卡申请单通知
    public void cardApplyNotify(Long id) {
    }

    // 卡激活
    public Result<CardActivateRes> cardActivate(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardActivateReq request = apiService.<CardActivateReq>initRequest(CardActivateReq.class, context, merchantId, reqId, name, body, sign);

        TCardActivateRequest tCardActivateRequest = ConvertUtils.sourceToTarget(request, TCardActivateRequest.class);
        TCardActivateResponse tCardActivateResponse = zinCardStatusService.cardActivate(tCardActivateRequest);
        CardActivateRes cardActivateRes = ConvertUtils.sourceToTarget(tCardActivateResponse, CardActivateRes.class);

        Result<CardActivateRes> result = new Result<>();
        result.setData(cardActivateRes);
        return result;
    }

    // 卡支付信息
    public Result<CardPayInfoRes> cardPayInfo(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardPayInfoReq request = apiService.<CardPayInfoReq>initRequest(CardPayInfoReq.class, context, merchantId, reqId, name, body, sign);

        TCardPayInfoRequest tCardPayInfoRequest = ConvertUtils.sourceToTarget(request, TCardPayInfoRequest.class);
        TCardPayInfoResponse tCardPayInfoResponse = zinCardApplyService.cardPayInfo(tCardPayInfoRequest);
        CardPayInfoRes cardPayInfoRes = ConvertUtils.sourceToTarget(tCardPayInfoResponse, CardPayInfoRes.class);

        Result<CardPayInfoRes> result = new Result<>();
        result.setData(cardPayInfoRes);
        return result;
    }

    // 卡状态变更申请
    public Result cardChange(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardChangeReq request = apiService.<CardChangeReq>initRequest(CardChangeReq.class, context, merchantId, reqId, name, body, sign);

        // 准备应答
        TResult tResult = null;

        switch (request.getChangetype()) {
            case CardConstant.CARD_LOSS:
                TCardLossRequest tCardLossRequest = ConvertUtils.sourceToTarget(request, TCardLossRequest.class);
                tResult = zinCardStatusService.cardLoss(tCardLossRequest);
                break;
            case CardConstant.CARD_UNLOSS:
                TCardUnlossRequest tCardUnlossRequest = ConvertUtils.sourceToTarget(request, TCardUnlossRequest.class);
                tResult = zinCardStatusService.cardUnloss(tCardUnlossRequest);
                break;
            case CardConstant.CARD_CANCEL:
                TCardCancelRequest tCardCancelRequest = ConvertUtils.sourceToTarget(request, TCardCancelRequest.class);
                tResult = zinCardStatusService.cardCancel(tCardCancelRequest);
                break;
            case CardConstant.CARD_UNCANCEL:
                TCardUncancelRequest tCardUncancelRequest = ConvertUtils.sourceToTarget(request, TCardUncancelRequest.class);
                tResult = zinCardStatusService.cardUncancel(tCardUncancelRequest);
                break;
            case CardConstant.CARD_FREEZE:
                TCardFreezeRequest tCardFreezeRequest = ConvertUtils.sourceToTarget(request, TCardFreezeRequest.class);
                tResult = zinCardStatusService.cardFreeze(tCardFreezeRequest);
                break;
            case CardConstant.CARD_UNFREEZE:
                TCardUnfreezeRequest tCardUnfreezeRequest = ConvertUtils.sourceToTarget(request, TCardUnfreezeRequest.class);
                tResult = zinCardStatusService.cardUnfreeze(tCardUnfreezeRequest);
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
    public Result<CardBalanceRes> cardBalance(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardBalanceReq request = apiService.<CardBalanceReq>initRequest(CardBalanceReq.class, context, merchantId, reqId, name, body, sign);
        TCardBalanceRequest tCardBalanceRequest = ConvertUtils.sourceToTarget(request, TCardBalanceRequest.class);

        TCardBalanceResponse balance = zinCardMoneyService.balance(tCardBalanceRequest);
        CardBalanceRes cardBalanceRes = ConvertUtils.sourceToTarget(balance, CardBalanceRes.class);

        Result<CardBalanceRes> result = new Result<>();
        result.setData(cardBalanceRes);
        return result;
    }

    // 卡状态变更通知
    public void cardStateNotify(Long id) {
    }

    // 卡保证金充值
    public Result<CardDepositRes> cardDeposit(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardDepositReq request = apiService.<CardDepositReq>initRequest(CardDepositReq.class, context, merchantId, reqId, name, body, sign);

        TDepositRequest tDepositRequest = ConvertUtils.sourceToTarget(request, TDepositRequest.class);
        TDepositResponse deposit = zinCardMoneyService.deposit(tDepositRequest);
        CardDepositRes cardDepositRes = ConvertUtils.sourceToTarget(deposit, CardDepositRes.class);

        Result<CardDepositRes> result = new Result<>();
        result.setData(cardDepositRes);
        return result;
    }

    // 卡保证金提现
    public Result<CardWithdrawRes> cardWithdraw(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        CardWithdrawReq request = apiService.<CardWithdrawReq>initRequest(CardWithdrawReq.class, context, merchantId, reqId, name, body, sign);

        TWithdrawRequest tWithdrawRequest = ConvertUtils.sourceToTarget(request, TWithdrawRequest.class);
        TWithdrawResponse withdraw = zinCardMoneyService.withdraw(tWithdrawRequest);
        CardWithdrawRes cardWithdrawRes = ConvertUtils.sourceToTarget(withdraw, CardWithdrawRes.class);

        Result<CardWithdrawRes> result = new Result<>();
        result.setData(cardWithdrawRes);
        return result;
    }
}
