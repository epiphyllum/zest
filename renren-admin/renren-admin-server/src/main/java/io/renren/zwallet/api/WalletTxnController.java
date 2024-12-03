package io.renren.zwallet.api;

import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.service.JAuthService;
import io.renren.zadmin.service.JAuthedService;
import io.renren.zadmin.service.JWalletTxnService;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.*;
import io.renren.zwallet.manager.JWalletInfoManager;
import io.renren.zwallet.manager.JWalletTxnManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("zwallet/txn")
@RestController
public class WalletTxnController {
    @Resource
    private JWalletTxnManager jWalletTxnManager;
    @Resource
    private JWalletInfoManager jWalletInfoManager;
    @Resource
    private JWalletTxnService jWalletTxnService;
    @Resource
    private JAuthService jAuthService;
    @Resource
    private JAuthedService jAuthedService;

    // 钱包充值: 返回一个payUrl
    @PostMapping("charge")
    public Result<WalletChargeResponse> charge(@RequestBody WalletChargeRequest request) {
        WalletChargeResponse response = jWalletTxnManager.charge(request);
        Result<WalletChargeResponse> result = new Result<>();
        result.setData(response);
        return result;
    }

    // 钱包充值
    @PostMapping("withdraw")
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
    @GetMapping("page/wallet")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<WalletTxnItem>> pageWallet(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<WalletTxnItem> walletTxnItemPageData = jWalletTxnService.walletPage(params);
        Result<PageData<WalletTxnItem>> result = new Result<>();
        result.setData(walletTxnItemPageData);
        return result;
    }

    // 卡交易列表
    @GetMapping("page/card")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<WalletCardTxnItem>> pageAuth(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<WalletCardTxnItem> walletCardTxnItemPageData = jAuthService.walletPage(params);
        Result<PageData<WalletCardTxnItem>> result = new Result<>();
        result.setData(walletCardTxnItemPageData);
        return result;
    }

    // 卡交易列表(已经)
    @GetMapping("page/authed")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<WalletCardTxnItem>> pageAuthed(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<WalletCardTxnItem> walletCardTxnItemPageData = jAuthedService.walletPage(params);
        Result<PageData<WalletCardTxnItem>> result = new Result<>();
        result.setData(walletCardTxnItemPageData);
        return result;
    }

}
