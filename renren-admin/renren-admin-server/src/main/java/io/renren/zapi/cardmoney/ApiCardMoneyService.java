package io.renren.zapi.cardmoney;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.cardmoney.dto.*;
import io.renren.zin.cardmoney.dto.TDepositRequest;
import io.renren.zin.cardmoney.dto.TDepositResponse;
import io.renren.zin.cardmoney.dto.TWithdrawRequest;
import io.renren.zin.cardmoney.dto.TWithdrawResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCardMoneyService {

    @Resource
    private ZinCardMoneyService zinCardMoneyService;

    // 卡保证金充值
    public Result<CardChargeRes> cardCharge(CardChargeReq request, ApiContext context) {
        TDepositRequest tDepositRequest = ConvertUtils.sourceToTarget(request, TDepositRequest.class);
        TDepositResponse deposit = zinCardMoneyService.deposit(tDepositRequest);
        CardChargeRes cardChargeRes = ConvertUtils.sourceToTarget(deposit, CardChargeRes.class);

        Result<CardChargeRes> result = new Result<>();
        result.setData(cardChargeRes);
        return result;
    }
    public Result<CardChargeQueryRes> cardChargeQuery(CardChargeQuery request, ApiContext context) {
        return null;
    }

    // 卡保证金提现
    public Result<CardWithdrawRes> cardWithdraw(CardWithdrawReq request, ApiContext context) {

        TWithdrawRequest tWithdrawRequest = ConvertUtils.sourceToTarget(request, TWithdrawRequest.class);
        TWithdrawResponse withdraw = zinCardMoneyService.withdraw(tWithdrawRequest);
        CardWithdrawRes cardWithdrawRes = ConvertUtils.sourceToTarget(withdraw, CardWithdrawRes.class);

        Result<CardWithdrawRes> result = new Result<>();
        result.setData(cardWithdrawRes);
        return result;
    }

    public Result<CardWithdrawQueryRes> cardWithdrawQuery(CardWithdrawQuery request, ApiContext context) {
        return null;
    }
}
