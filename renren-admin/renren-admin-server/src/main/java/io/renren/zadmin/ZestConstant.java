package io.renren.zadmin;

import io.renren.commons.security.user.SecurityUser;

public class ZestConstant {

    // 用户类型
    public static final String USER_TYPE_OPERATION = "operation";
    public static final String USER_TYPE_AGENT = "agent";
    public static final String USER_TYPE_MERCHANT = "merchant";
    public static final String USER_TYPE_SUB = "sub";

    public static boolean isOperation() {
        return USER_TYPE_OPERATION.equals(SecurityUser.getUser().getUserType());
    }

    public static boolean isAgent() {
        return USER_TYPE_AGENT.equals(SecurityUser.getUser().getUserType());
    }

    public static boolean isMerchant() {
        return USER_TYPE_MERCHANT.equals(SecurityUser.getUser().getUserType());
    }

    public static boolean isSub() {
        return USER_TYPE_SUB.equals(SecurityUser.getUser().getUserType());
    }

    public static boolean isOperationOrAgent() {
        String userType = SecurityUser.getUser().getUserType();
        return USER_TYPE_AGENT.equals(userType) || USER_TYPE_OPERATION.equals(userType);
    }

    public static boolean isOperationOrAgentOrMerchant() {
        String userType = SecurityUser.getUser().getUserType();
        return USER_TYPE_AGENT.equals(userType) || USER_TYPE_OPERATION.equals(userType) || USER_TYPE_MERCHANT.equals(userType);
    }

    public static boolean isMerchantOrSub() {
        String userType = SecurityUser.getUser().getUserType();
        return USER_TYPE_MERCHANT.equals(userType) || USER_TYPE_SUB.equals(userType);
    }


}
