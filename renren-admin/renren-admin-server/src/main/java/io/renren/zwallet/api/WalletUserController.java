package io.renren.zwallet.api;


import io.renren.commons.tools.utils.Result;
import io.renren.zmanager.JWalletManager;
import io.renren.zwallet.dto.WalletInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wallet/api/user")
public class WalletUserController {
    @Resource
    private JWalletManager jWalletManager;

    /**
     * 用户登录
     */
    @GetMapping("login")
    public Result<String> login(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("otp") String otp
    ) {
        String token = jWalletManager.login(phone, password, otp);
        Result<String> result = new Result<>();
        result.setData(token);
        return result;
    }

    /**
     * 注册
     */
    @GetMapping("register")
    public Result register(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("otp") String otp
    ) {
        jWalletManager.register(email, password, otp);
        return new Result();
    }

    /**
     * 发送otp
     */
    @GetMapping("emailOTP")
    public Result<String> emailOTP(@RequestParam("email") String email) {
        jWalletManager.emailOTP(email);
        Result<String> result = new Result<>();
        result.setData("success");
        return result;
    }
}
