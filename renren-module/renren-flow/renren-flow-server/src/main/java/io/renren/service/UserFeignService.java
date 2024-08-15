package io.renren.service;

import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.utils.Result;
import io.renren.feign.UserFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserFeignService {
    private final UserFeignClient userFeignClient;

    /**
     * 根据用户ID，获取用户信息
     */
    public String getUsernameById(String id) {
        Result<UserDetail> result = userFeignClient.getById(Long.parseLong(id));

        return result.getData().getUsername();
    }

    /**
     * 根据角色ID,查询用户ID列表
     */
    public List<Long> getUserIdListByRoleIdList(List<Long> ids) {
        Result<List<Long>> result = userFeignClient.getUserIdListByRoleIdList(ids);

        return result.getData();
    }

    /**
     * 根据岗位ID,查询用户ID列表
     */
    public List<Long> getUserIdListByPostIdList(List<Long> ids) {
        Result<List<Long>> result = userFeignClient.getUserIdListByPostIdList(ids);

        return result.getData();
    }

    /**
     * 根据部门ID,查询部门领导列表
     */
    public List<Long> getLeaderIdListByDeptIdList(List<Long> ids) {
        Result<List<Long>> result = userFeignClient.getLeaderIdListByDeptIdList(ids);

        return result.getData();
    }

    /**
     * 根据部门ID,查询部门领导列表
     */
    public Long getLeaderIdListByUserId(Long userId) {
        Result<Long> result = userFeignClient.getLeaderIdListByUserId(userId);

        return result.getData();
    }

}
