package io.renren.zcommon;

import io.renren.commons.security.user.SecurityUser;

public class ZestConstant {
    // 账户属主类型
    public static final String USER_TYPE_OPERATION = "operation"; // 机构
    public static final String USER_TYPE_AGENT = "agent";         // 代理
    public static final String USER_TYPE_MERCHANT = "merchant";   // 商户
    public static final String USER_TYPE_SUB = "sub";             // 子商户
    public static final String USER_TYPE_PREPAID = "prepaid";     // 预付费卡
    public static final String USER_TYPE_WCARD = "wcard";         // 钱包卡
    public static final String USER_TYPE_WALLET = "wallet";       // 钱包账户

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

    public static final String BATCH_TYPE_STAT = "stat";     // 合并统计
    public static final String BATCH_TYPE_AUTHED = "authed"; // 结算流水

    // 屁处理任务状态
    public static final String BATCH_STATUS_NEW = "00";
    public static final String BATCH_STATUS_SUCCESS = "01";
    public static final String BATCH_STATUS_FAIL = "02";
}
