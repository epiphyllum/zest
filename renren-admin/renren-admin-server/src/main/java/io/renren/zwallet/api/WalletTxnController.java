package io.renren.zwallet.api;

import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.*;
import io.renren.zwallet.manager.JWalletInfoManager;
import io.renren.zwallet.manager.JWalletTxnManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("zwallet/txn")
@RestController
public class WalletTxnController {

    @Resource
    private JWalletTxnManager jWalletTxnManager;
    @Resource
    private JWalletInfoManager jWalletInfoManager;


    // 钱包充值: 返回一个payUrl
    @RequestMapping("charge")
    public Result<WalletChargeResponse> charge(@RequestBody WalletChargeRequest request) {
        WalletChargeResponse response = jWalletTxnManager.charge(request);
        Result<WalletChargeResponse> result = new Result<>();
        result.setData(response);
        return result;
    }

    // 钱包充值
    @RequestMapping("withdraw")
    public Result<WalletWithdrawResponse> withdraw(@RequestBody WalletWithdrawRequest request) {
        WalletWithdrawResponse response = jWalletTxnManager.withdraw(request);
        Result<WalletWithdrawResponse> result = new Result<>();
        result.setData(response);
        return result;
    }

    // 钱包余额， 卡列表
    @GetMapping("walletInfo")
    public Result<WalletInfo> walletInfo() {
        JWalletEntity walletEntity = WalletLoginInterceptor.walletUser();
        WalletInfo info = jWalletInfoManager.walletInfo(walletEntity);
        Result<WalletInfo> result = new Result<>();
        result.setData(info);
        return result;
    }

    // 钱包交易列表
    @RequestMapping("list")
    public Result<List<WalletTxnItem>> list() {
        return null;
    }
}
