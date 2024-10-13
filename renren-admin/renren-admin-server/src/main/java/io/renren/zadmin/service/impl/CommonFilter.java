package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.service.SysDeptService;
import io.renren.zcommon.ZestConstant;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 按操作用的类型过滤
@Service
public class CommonFilter {
    @Resource
    private SysDeptService sysDeptService;

    public void setFilterAll(QueryWrapper<?> wrapper, Map<String, Object> params) {
        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

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

    public  void setFilterMerchant(QueryWrapper<?> wrapper, Map<String, Object> params) {
        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

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

    public void setFilterAgent(QueryWrapper<?> wrapper, Map<String, Object> params) {
        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

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

    public void setLogBalanceFilter(QueryWrapper<?> wrapper, Map<String, Object> params) {
        // ownerId优先,  如果有， 就只看ownerId的
        String ownerId = (String) params.get("ownerId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(ownerId)) {
            wrapper.eq("owner_id", Long.parseLong(ownerId));
        } else {
            // 没有ownerId, 就看选到哪个层次， 就看那个层次下所有的
            String rootId = null;
            String subId = (String) params.get("subId");
            String merchantId = (String) params.get("merchantId");
            String agentId = (String) params.get("agentId");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(subId)) {
                rootId = subId;
            } else if (StringUtils.isNotBlank(merchantId)) {
                rootId = merchantId;
            } else if (org.apache.commons.lang3.StringUtils.isNotBlank(agentId)) {
                rootId = agentId;
            }
            if (StringUtils.isNotBlank(rootId)) {
                Long top = Long.parseLong(rootId);
                List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(top);
                wrapper.in("owner_id", subDeptIdList);
            }
        }
    }
}
