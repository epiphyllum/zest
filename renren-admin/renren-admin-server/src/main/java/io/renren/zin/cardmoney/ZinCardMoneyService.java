package io.renren.zin.cardmoney;

import io.renren.zin.ZinRequester;
import io.renren.zin.cardmoney.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static io.renren.zcommon.CommonUtils.newRequestId;

@Service
public class ZinCardMoneyService {
    @Resource
    private ZinRequester requester;

    // 3100-保证金缴纳
    public TDepositResponse deposit(TDepositRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/deposit", request, TDepositResponse.class);
    }

    // 3101-保证金提取
    public TWithdrawResponse withdraw(TWithdrawRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/withdraw", request, TWithdrawResponse.class);
    }

    // 3102-卡余额查询
    public TCardBalanceResponse balance(TCardBalanceRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/querybalance", request, TCardBalanceResponse.class);
    }

    // 3103-卡保证金存提记录查询
    public TCardBondResponse querybond(TCardBondQuery request) {
        return requester.request(newRequestId(), "/gcpapi/card/querybond", request, TCardBondResponse.class);
    }
}
