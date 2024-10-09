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
}