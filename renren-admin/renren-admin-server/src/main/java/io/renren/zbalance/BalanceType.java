package io.renren.zbalance;

import java.util.List;

public class BalanceType {
    // 币种列表
    public static List<String> CURRENCY_LIST = List.of(
            "HKD", "USD", "GBP", "JPY", "CAD"
            , "AUD", "SGD", "SEK", "CHF", "DKK"
            , "NOK", "ZAR", "EUR", "NZD", "CNY"
    );

    // 商户VA
    public static String getVaAccount(String currency) {
        return "VA_" + currency;
    }
    // 商户保证金
    public static String getDepositAccount(String currency) {
        return "DEPOSIT_" + currency;
    }
    // 商户卡充值手续费
    public static String getChargeFeeAccount(String currency) {
        return "CHARGE_FEE_" + currency;
    }
    // 商户交易费用
    public static String getTxnFeeAccount(String currency) {
        return "TXN_FEE_" + currency;
    }
    // 子商户VA
    public static String getSubVaAccount(String currency) {
        return "SUB_VA_" + currency;
    }
    // 子商户卡汇总充值资金
    public static String getSubSumAccount(String currency) {
        return "SUB_SUM_" + currency;
    }
    // 子商户开卡费用
    public static String getSubFeeAccount(String currency) {
        return "SUB_FEE_" + currency;
    }
    // 预付费卡主卡额度
    public static String getPrepaidAccount(String currency) {
        return "PREPAID_" + currency;
    }
    // 预付费卡主卡发卡总额
    public static String getPrepaidSumAccount(String currency) {
        return "PREPAID_SUM_" + currency;
    }

    // 累计发起充值金额
    public static String getChargeSumAccount(String currency) {
        return "CHARGE_SUM_" + currency;
    }

    // 累计保证金
    public static String getDepositSumAccount(String currency) {
        return "DEPOSIT_SUM_" + currency;
    }

    // 累计手续费
    public static String getFeeSumAccount(String currency) {
        return "FEE_SUM_" + currency;
    }
}