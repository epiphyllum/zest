package io.renren.security.event;

import io.renren.commons.log.SysLogLogin;
import io.renren.commons.log.enums.LogTypeEnum;
import io.renren.commons.log.enums.LoginOperationEnum;
import io.renren.commons.log.producer.LogProducer;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.utils.HttpContextUtils;
import io.renren.commons.tools.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 认证事件处理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
@AllArgsConstructor
public class AuthenticationEvents {
    private final LogProducer logProducer;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        // 用户信息
        UserDetail user = (UserDetail) event.getAuthentication().getPrincipal();

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();

        // 登录成功日志
        SysLogLogin log = new SysLogLogin();
        log.setType(LogTypeEnum.LOGIN.value());
        log.setOperation(LoginOperationEnum.SUCCESS.value());
        log.setIp(IpUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setIp(IpUtils.getIpAddr(request));
        log.setCreatorName(user.getUsername());
        log.setCreateDate(new Date());
        logProducer.saveLog(log);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        // 用户名
        String username = (String) event.getAuthentication().getPrincipal();

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();

        // 登录失败日志
        SysLogLogin log = new SysLogLogin();
        log.setType(LogTypeEnum.LOGIN.value());
        log.setOperation(LoginOperationEnum.FAIL.value());
        log.setIp(IpUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setIp(IpUtils.getIpAddr(request));
        log.setCreatorName(username);
        log.setCreateDate(new Date());
        logProducer.saveLog(log);
    }

}