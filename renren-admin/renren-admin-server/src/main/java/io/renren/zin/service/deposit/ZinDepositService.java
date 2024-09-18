package io.renren.zin.service.deposit;

import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.deposit.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
public class ZinDepositService {
    @Resource
    private ZinRequester requester;

    // 保证金缴纳
    public TDepositResponse deposit(TDepositRequest reqeust) {
        return requester.request(newRequestId(), "/gcpapi/card/deposit", reqeust, TDepositResponse.class);
    }

    // 保证金提取
    public TWithdrawResponse withdraw(TWithdrawRequest reqeust) {
        return requester.request(newRequestId(), "/gcpapi/card/withdraw", reqeust, TWithdrawResponse.class);
    }

    // 卡余额查询
    public TCardBalanceResponse balance(TCardBalanceRequest reqeust) {
        return requester.request(newRequestId(), "/gcpapi/card/querybalance", reqeust, TCardBalanceResponse.class);
    }

    // 卡保证金存提记录查询: todo: /gcpapi/card/querybond
    public void todo() {
    }
}
