/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.controller;

import io.renren.commons.log.SysLogLogin;
import io.renren.commons.log.enums.LogTypeEnum;
import io.renren.commons.log.enums.LoginOperationEnum;
import io.renren.commons.log.producer.LogProducer;
import io.renren.commons.security.cache.TokenStoreCache;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.security.utils.TokenUtils;
import io.renren.commons.tools.exception.ErrorCode;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.redis.RedisKeys;
import io.renren.commons.tools.redis.RedisUtils;
import io.renren.commons.tools.utils.IpUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.dto.LoginDTO;
import io.renren.dto.UserTokenDTO;
import io.renren.service.CaptchaService;
import io.renren.service.SysUserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

/**
 * 用户登录
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@AllArgsConstructor
@Tag(name = "用户登录")
@RequestMapping("auth")
public class LoginController {
    private final CaptchaService captchaService;
    private final TokenStoreCache tokenStoreCache;
    private final AuthenticationManager authenticationManager;
    private final SysUserTokenService sysUserTokenService;
    private final LogProducer logProducer;
    private final RedisUtils redisUtils;

    @GetMapping("captcha")
    @Operation(summary = "验证码")
    @Parameter(name = "uuid", required = true)
    public void captcha(HttpServletResponse response, String uuid) throws IOException {
        //uuid不能为空
        AssertUtils.isBlank(uuid, ErrorCode.IDENTIFIER_NOT_NULL);

        //生成验证码
        captchaService.create(response, uuid);
    }

    @ResponseBody
    @PostMapping("login")
    @Operation(summary = "账号密码登录")
    public Result<UserTokenDTO> login(@RequestBody LoginDTO login) {
        // 验证码效验
        boolean flag = captchaService.validate(login.getUuid(), login.getCaptcha());
        if (!flag) {
            throw new RenException("验证码错误");
        }

        Authentication authentication;
        try {
            // 用户认证
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
        } catch (Exception e) {
            throw new RenException("用户名或密码错误");
        }

        // 用户信息
        UserDetail user = (UserDetail) authentication.getPrincipal();

        // 生成 accessToken
        UserTokenDTO userTokenVO = sysUserTokenService.createToken(user.getId());

        // 保存用户信息到缓存
        tokenStoreCache.saveUser(userTokenVO.getAccessToken(), user);

        return new Result<UserTokenDTO>().ok(userTokenVO);
    }

    @ResponseBody
    @PostMapping("access-token")
    @Operation(summary = "刷新 access_token")
    public Result<UserTokenDTO> getAccessToken(String refreshToken) {
        UserTokenDTO token = sysUserTokenService.refreshToken(refreshToken);

        return new Result<UserTokenDTO>().ok(token);
    }

    @ResponseBody
    @PostMapping("logout")
    @Operation(summary = "退出")
    public Result logout(HttpServletRequest request) {
        String accessToken = TokenUtils.getAccessToken(request);

        // 用户信息
        UserDetail user = tokenStoreCache.getUser(accessToken);

        // 删除用户信息
        tokenStoreCache.deleteUser(accessToken);

        // Token过期
        sysUserTokenService.expireToken(user.getId());

        // 保存日志
        SysLogLogin log = new SysLogLogin();
        log.setType(LogTypeEnum.LOGIN.value());
        log.setOperation(LoginOperationEnum.LOGOUT.value());
        log.setIp(IpUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setIp(IpUtils.getIpAddr(request));
        log.setCreator(user.getId());
        log.setCreatorName(user.getUsername());
        log.setCreateDate(new Date());
        logProducer.saveLog(log);

        //清空菜单导航、权限标识
        redisUtils.deleteByPattern(RedisKeys.getUserMenuNavKey(user.getId()));
        redisUtils.delete(RedisKeys.getUserPermissionsKey(user.getId()));

        return new Result();
    }
}
