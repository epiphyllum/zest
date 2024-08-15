/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.renren.commons.tools.redis.RedisKeys;
import io.renren.commons.tools.redis.RedisUtils;
import io.renren.service.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
@AllArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final RedisUtils redisUtils;

    @Override
    public void create(HttpServletResponse response, String uuid) throws IOException {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        //生成验证码
        SpecCaptcha captcha = new SpecCaptcha(150, 40);
        captcha.setLen(5);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        captcha.out(response.getOutputStream());

        //保存验证码
        setCache(uuid, captcha.text());
    }

    @Override
    public boolean validate(String uuid, String code) {
        String captcha = getCache(uuid);

        //验证码是否正确
        return code.equalsIgnoreCase(captcha);
    }

    private void setCache(String uuid, String captcha) {
        String key = RedisKeys.getLoginCaptchaKey(uuid);

        redisUtils.set(key, captcha, 60 * 5L);
    }

    private String getCache(String uuid) {
        String key = RedisKeys.getLoginCaptchaKey(uuid);
        String captcha = (String) redisUtils.get(key);

        // 删除验证码
        if (captcha != null) {
            redisUtils.delete(key);
        }

        return captcha;
    }
}