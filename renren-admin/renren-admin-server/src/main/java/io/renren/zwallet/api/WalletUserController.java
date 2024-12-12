package io.renren.zwallet.api;


import io.renren.commons.tools.utils.Result;
import io.renren.zwallet.dto.WalletConfigInfo;
import io.renren.zwallet.dto.WalletLoginRequest;
import io.renren.zwallet.manager.JWalletUserManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static io.renren.zwallet.config.WalletLoginInterceptor.walletUser;

@RestController
@RequestMapping("zwallet/api/user")
public class WalletUserController {
    @Resource
    private JWalletUserManager jWalletUserManager;

    /**
     * 注册
     */
    @PostMapping("register")
    public Result<String> register(@RequestBody WalletLoginRequest request) {
        String token = jWalletUserManager.register(request);
        Result<String> result = new Result<>();
        result.setData(token);
        return result;
    }

    /**
     * 用户登录
     */
    @PostMapping("login")
    public Result<String> login(@RequestBody WalletLoginRequest request) {
        String token = jWalletUserManager.login(request);
        Result<String> result = new Result<>();
        result.setData(token);
        return result;
    }

    /**
     * 发送otp
     */
    @GetMapping("emailOTP")
    public Result emailOTP(@RequestParam("email") String email) {
        jWalletUserManager.emailOTP(email);
        return new Result();
    }

    /**
     * 修改密码
     */
    @GetMapping("change")
    public Result change(@RequestParam("newPass") String newPass, @RequestParam("otp") String otp) {
        jWalletUserManager.change(newPass, otp);
        return new Result();
    }

    /**
     * 重置密码
     */
    @GetMapping("reset")
    public Result reset(@RequestParam("email") String email) {
        jWalletUserManager.reset(email);
        return new Result();
    }

    /**
     * 秘钥设置
     */
    @GetMapping("accessKey")
    public Result accessKey(@RequestParam("accessKey") String accessKey) {
        jWalletUserManager.setAccessKey(accessKey, walletUser());
        return new Result();
    }

    /**
     * 全局配置
     */
    @GetMapping("config")
    public Result<WalletConfigInfo> config() {
        WalletConfigInfo walletConfigInfo = jWalletUserManager.walletConfigInfo(walletUser());
        Result<WalletConfigInfo> result = new Result<>();
        result.setData(walletConfigInfo);
        return result;
    }

}
