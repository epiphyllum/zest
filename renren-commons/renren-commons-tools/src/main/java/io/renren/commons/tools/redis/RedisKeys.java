/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.commons.tools.redis;

/**
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public class RedisKeys {
    /**
     * 系统参数Key
     */
    public static String getSysParamsKey(){
        return "sys:params";
    }

    /**
     * 登录验证码Key
     */
    public static String getLoginCaptchaKey(String uuid){
        return "sys:captcha:" + uuid;
    }

    /**
     * 系统日志Key
     */
    public static String getSysLogKey(){
        return "sys:log";
    }

    /**
     * 用户菜单导航Key
     */
    public static String getUserMenuNavKey(Long userId, String language){
        return "sys:user:nav:" + userId + "_" + language;
    }

    /**
     * 用户菜单导航Key
     */
    public static String getUserMenuNavKey(Long userId){
        return "sys:user:nav:" + userId + "_*";
    }

    /**
     * 用户权限标识Key
     */
    public static String getUserPermissionsKey(Long userId){
        return "sys:user:permissions:" + userId;
    }
}
