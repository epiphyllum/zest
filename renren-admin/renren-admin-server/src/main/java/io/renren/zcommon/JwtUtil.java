package io.renren.zcommon;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.renren.zadmin.entity.JWalletEntity;

import java.util.Date;

public class JwtUtil {
    private static final String KEY = "wingo@977100";
    //接收业务数据,生成token并返回
    // withClaim 配置有效载荷
    // withExpiresAt 配置过期时间
    // sign 配置加密算法和密钥
    public static String genToken(JWalletEntity userEntity) {
        return JWT.create()
                .withClaim("claims", userEntity.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .sign(Algorithm.HMAC256(KEY));
    }

    //接收token,验证token,并返回业务数据
    public static Long parseToken(String token) {
        Long userId = JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asLong();
        return userId;
    }
}