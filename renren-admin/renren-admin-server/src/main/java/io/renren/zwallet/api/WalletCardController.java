package io.renren.zwallet.api;

import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletCardChargeRequest;
import io.renren.zwallet.dto.WalletCardOpenRequest;
import io.renren.zwallet.dto.WalletCardWithdrawRequest;
import io.renren.zwallet.manager.JWalletCardManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("zwallet/api/card")
public class WalletCardController {
    @Resource
    private JWalletCardManager jWalletCardManager;

    /**
     * 开卡
     */
    @PostMapping("openVpa")
    public Result<Long> openVpa(@RequestBody WalletCardOpenRequest request) {
        Long jobId = jWalletCardManager.openVpa(request, WalletLoginInterceptor.walletUser());
        Result<Long> result = new Result<>();
        result.setData(jobId);
        return result;
    }

    /**
     * 实名卡|实体卡
     */
    @PostMapping("openVcc")
    public Result<Long> openVcc(@RequestBody JCardEntity entity) {
        Long jobId = jWalletCardManager.openVcc(entity, WalletLoginInterceptor.walletUser());
        Result<Long> result = new Result<>();
        result.setData(jobId);
        return result;
    }

    /**
     * 开卡查询: 匿名卡
     */
    @GetMapping("openVpaQuery")
    public Result<String> openVpaQuery(@RequestParam("id") Long id) {
        String state = jWalletCardManager.openVpaQuery(id, WalletLoginInterceptor.walletUser());
        Result<String> result = new Result<>();
        result.setData(state);
        return result;
    }

    /**
     * 开卡查询: 实名卡、实体卡
     */
    @GetMapping("openQuery")
    public Result openVccQuery(@RequestParam("id") Long id) {
        jWalletCardManager.openVccQuery(id, WalletLoginInterceptor.walletUser());
        return new Result();
    }

    /**
     * 发起卡充值
     */
    @PostMapping("charge")
    public Result charge(@RequestBody WalletCardChargeRequest request) {
        jWalletCardManager.chargeCard(request, WalletLoginInterceptor.walletUser());
        Result<String> result = new Result();
        return result;
    }

    /**
     * 卡提现
     */
    @PostMapping("withdraw")
    public Result withdraw(@RequestBody WalletCardWithdrawRequest request) {
        jWalletCardManager.withdrawCard(request, WalletLoginInterceptor.walletUser());
        return new Result();
    }

    /**
     * 查询卡余额
     */
    @GetMapping("balance")
    public Result<BigDecimal> balance(@RequestParam("cardno") String cardno) {
        BigDecimal balance = jWalletCardManager.balance(cardno);
        Result<BigDecimal> result = new Result<>();
        result.setData(balance);
        return result;
    }
}
