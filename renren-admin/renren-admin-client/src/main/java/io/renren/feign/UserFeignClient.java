/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.feign;

import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.ServiceConstant;
import io.renren.commons.tools.utils.Result;
import io.renren.feign.fallback.UserFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@FeignClient(name = ServiceConstant.RENREN_ADMIN_SERVER, contextId = "UserFeignClient", fallbackFactory = UserFeignClientFallbackFactory.class)
public interface UserFeignClient {

    /**
     * 根据用户ID，获取用户信息
     */
    @GetMapping("sys/user/getById")
    Result<UserDetail> getById(@RequestParam("id") Long id);

    /**
     * 根据用户ID，获取角色Id列表
     */
    @GetMapping("sys/role/getRoleIdList")
    Result<List<Long>> getRoleIdList(@RequestParam("userId") Long userId);


    /**
     * 根据角色ID,查询用户ID列表
     */
    @PostMapping("sys/user/getUserIdListByRoleIdList")
    Result<List<Long>> getUserIdListByRoleIdList(@RequestParam List<Long> ids);

    /**
     * 根据岗位ID,查询用户ID列表
     */
    @PostMapping("sys/user/getUserIdListByPostIdList")
    Result<List<Long>> getUserIdListByPostIdList(@RequestParam List<Long> ids);

    /**
     * 根据部门ID,查询部门领导列表
     */
    @PostMapping("sys/user/getLeaderIdListByDeptIdList")
    Result<List<Long>> getLeaderIdListByDeptIdList(@RequestParam List<Long> ids);


    /**
     * 根据用户ID,查询部门领导ID
     */
    @PostMapping("sys/user/getLeaderIdListByUserId")
    Result<Long> getLeaderIdListByUserId(@RequestParam("userId") Long userId);


}