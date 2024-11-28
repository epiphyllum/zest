package io.renren.zwallet.api;

import io.renren.commons.tools.utils.Result;
import io.renren.zwallet.dto.WalletCardMoneyItem;
import io.renren.zwallet.manager.JWalletCardManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("zwallet/card")
public class WalletCardController {

    @Resource
    private JWalletCardManager jWalletCardManager;

    /**
     * 开卡
     */
    @RequestMapping("open")
    public void open() {
    }

    /**
     * 发起卡充值
     */
    @RequestMapping("charge")
    public Result<String> charge(@RequestParam("amount") BigDecimal amount, @RequestParam("cardno") String cardno) {
        String payUrl = jWalletCardManager.charge(amount, cardno);
        Result<String> result = new Result();
        result.setData(payUrl);
        return result;
    }

    /**
     * 卡提现
     */
    @RequestMapping("withdraw")
    public Result withdraw(@RequestParam("amount") BigDecimal amount, @RequestParam("cardno") String cardno) {
        jWalletCardManager.withdraw(amount, cardno);
        return new Result();
    }

    /**
     * 卡充值提现列表
     */
    @RequestMapping("transactions")
    public Result<List<WalletCardMoneyItem>> list() {
        return null;
    }
}
