package io.renren.zwallet.config;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zcommon.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class WalletLoginInterceptor implements HandlerInterceptor {

//    @Resource
//    private WingoConfig wingoConfig;

    private JWalletDao jWalletDao;


    public static final ThreadLocal<JWalletEntity> threadLocal = new ThreadLocal<>();

    public static JWalletEntity walletUser() {
        JWalletEntity walletEntity = threadLocal.get();
        if (walletEntity == null) {
            throw new RenException("invalid wingo user");
        }
        return walletEntity;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        try {
            Long userId = JwtUtil.parseToken(token);
            JWalletEntity walletEntity = jWalletDao.selectById(userId);
            if (walletEntity == null) {
                log.error("用户不存在: {}", userId);
                throw new RenException("非法用户");
            }

//            // 防止夸机构的数据错乱!!!!
//            Map<String, String> mapping = wingoConfig.getDeptMapping();
//            if (mapping == null) {
//                log.error("can not get mapping from host: {}", IpUtils.getDomain());
//                throw new RenException("invalid user");
//            }
//            Long deptId = Long.parseLong(mapping.get("deptId"));
//            if (!walletEntity.getDeptId().equals(deptId)) {
//                log.error("dept does not match");
//                throw new RenException("invalid user");
//            }

            threadLocal.set(walletEntity);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}