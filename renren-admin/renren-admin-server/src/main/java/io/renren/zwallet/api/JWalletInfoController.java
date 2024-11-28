package io.renren.zwallet.api;

import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletInfo;
import io.renren.zwallet.manager.JWalletInfoManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zwallet/info")
public class JWalletInfoController {

    @Resource
    private JWalletInfoManager jWalletInfoManager;

    // 钱包余额， 卡列表
    @GetMapping("walletInfo")
    public Result<WalletInfo> walletInfo() {
        JWalletEntity walletEntity = WalletLoginInterceptor.walletUser();
        WalletInfo info = jWalletInfoManager.walletInfo(walletEntity);
        Result<WalletInfo> result = new Result<>();
        result.setData(info);
        return result;
    }

}
