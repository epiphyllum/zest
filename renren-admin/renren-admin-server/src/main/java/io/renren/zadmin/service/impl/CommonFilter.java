package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.zadmin.ZestConstant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 按操作用的类型过滤
public class CommonFilter {

    public static void setFilterAll(QueryWrapper<?> wrapper, Map<String, Object> params) {
        Set<String> set = new HashSet<>();
        UserDetail user = SecurityUser.getUser();
        if (ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
            // 代理访问
            wrapper.eq("agent_id", user.getDeptId());
            set.add("agentId");
        } else if (ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
            // 商户访问
            wrapper.eq("merchant_id", user.getDeptId());
            set.add("merchantId");
        } else if (ZestConstant.USER_TYPE_SUB.equals(user.getUserType())) {
            // 子商户访问
            wrapper.eq("sub_id", user.getDeptId());
            set.add("subId");
        }

        // 查询条件
        if (!set.contains("agentId")) {
            String agentId = (String) params.get("agentId");
            if (StringUtils.isNotBlank(agentId)) {
                wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", Long.parseLong(agentId));
            }
        }

        if (!set.contains("merchantId")) {
            String merchantId = (String) params.get("merchantId");
            if (StringUtils.isNotBlank(merchantId)) {
                wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", Long.parseLong(merchantId));
            }
        }

        if (!set.contains("subId")) {
            String subId = (String) params.get("subId");
            if (StringUtils.isNotBlank(subId)) {
                wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", Long.parseLong(subId));
            }
        }

    }

    public static void setFilterMerchant(QueryWrapper<?> wrapper, Map<String, Object> params) {
        Set<String> set = new HashSet<>();
        UserDetail user = SecurityUser.getUser();
        if (ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
            // 代理访问
            wrapper.eq("agent_id", user.getDeptId());
            set.add("agentId");
        } else if (ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
            // 商户访问
            wrapper.eq("merchant_id", user.getDeptId());
            set.add("merchantId");
        }

        // 查询条件
        if (!set.contains("agentId")) {
            String agentId = (String) params.get("agentId");
            if (StringUtils.isNotBlank(agentId)) {
                wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", Long.parseLong(agentId));
            }
        }

        if (!set.contains("merchantId")) {
            String merchantId = (String) params.get("merchantId");
            if (StringUtils.isNotBlank(merchantId)) {
                wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", Long.parseLong(merchantId));
            }
        }
    }

    public static void setFilterAgent(QueryWrapper<?> wrapper , Map<String, Object> params) {
        Set<String> set = new HashSet<>();
        UserDetail user = SecurityUser.getUser();
        if (ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
            // 代理访问
            wrapper.eq("agent_id", user.getDeptId());
            set.add("agentId");
        }

        // 查询条件
        if (!set.contains("agentId")) {
            String agentId = (String) params.get("agentId");
            if (StringUtils.isNotBlank(agentId)) {
                wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", Long.parseLong(agentId));
            }
        }
    }

}
