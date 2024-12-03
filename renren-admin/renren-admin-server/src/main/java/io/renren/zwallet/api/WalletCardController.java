package io.renren.zwallet.api;

import io.renren.commons.tools.utils.Result;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletCardChargeRequest;
import io.renren.zwallet.dto.WalletCardMoneyItem;
import io.renren.zwallet.dto.WalletCardWithdrawRequest;
import io.renren.zwallet.dto.WalletCardOpenRequest;
import io.renren.zwallet.manager.JWalletCardManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("zwallet/card")
public class WalletCardController {

    @Resource
    private JWalletCardManager jWalletCardManager;

    /**
     * 开卡
     */
    @PostMapping("open")
    public Result<Long> open(@RequestBody WalletCardOpenRequest request) {
        Long jobId = jWalletCardManager.open(request, WalletLoginInterceptor.walletUser());
        Result<Long> result = new Result<>();
        result.setData(jobId);
        return result;
    }

    /**
     * 开卡查询
     */
    @GetMapping("openQuery")
    public Result openQuery(@RequestParam("id") Long id) {
        jWalletCardManager.openQuery(id, WalletLoginInterceptor.walletUser());
        return new Result();
    }

    /**
     * 发起卡充值
     */
    @PostMapping("charge")
    public Result charge(@RequestBody WalletCardChargeRequest request) {
        jWalletCardManager.charge(request, WalletLoginInterceptor.walletUser());
        Result<String> result = new Result();
        return result;
    }

    /**
     * 卡提现
     */
    @PostMapping("withdraw")
    public Result withdraw(@RequestBody WalletCardWithdrawRequest request) {
        jWalletCardManager.withdraw(request, WalletLoginInterceptor.walletUser());
        return new Result();
    }
}
