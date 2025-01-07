package io.renren.zbalance;

import java.util.List;

public class BalanceType {
    // 币种列表
    public static List<String> CURRENCY_LIST = List.of(
            // 法币
            "HKD", "USD", "GBP", "JPY", "CAD"
            , "AUD", "SGD", "SEK", "CHF", "DKK"
            , "NOK", "ZAR", "EUR", "NZD", "CNY",
            // 数字货币
            "USDT", "USDC", "BTC", "ETH"
    );

    // 商户VA
    public static String getVaAccount(String currency) {
        return "VA_" + currency;
    }

    /////////////////////////////////////////////////////////////////////////
    // 子商户账户
    /////////////////////////////////////////////////////////////////////////
    // 子商户VA
    public static String getSubVaAccount(String currency) {
        return "SUB_VA_" + currency;
    }

    // 子商户-保证金
    public static String getDepositAccount(String currency) {
        return "DEPOSIT_" + currency;
    }

    // 子商户-卡充值手续费(收入)
    public static String getChargeAccount(String currency) {
        return "CHARGE_" + currency;
    }

    // 子商户-开卡费用(收入)
    public static String getCardFeeAccount(String currency) {
        return "CARD_FEE_" + currency;
    }

    // 子商户-卡汇总充值资金
    public static String getCardSumAccount(String currency) {
        return "CARD_SUM_" + currency;
    }

    // 子商户-交易费用(收入)
    public static String getTxnAccount(String currency) {
        return "TXN_" + currency;
    }

    // 子商户-发卡总数
    public static String getCardCountAccount(String currency) {
        return "CARD_COUNT_" + currency;
    }

    // 子商户-Aip累计发起充值金额
    public static String getAipCardSumAccount(String currency) {
        return "AIP_CARD_SUM_" + currency;
    }

    // 子商户-Aip-累计开卡手续费(成本)
    public static String getAipCardFeeAccount(String currency) {
        return "AIP_CARD_FEE_" + currency;
    }

    // 子商户-Aip-累计保证金()
    public static String getAipDepositAccount(String currency) {
        return "AIP_DEPOSIT_" + currency;
    }

    // 子商户-Aip-累计充值费用(成本)
    public static String getAipChargeAccount(String currency) {
        return "AIP_CHARGE_" + currency;
    }

    // 子商户-Aip-累计手续费(成本)
    public static String getAipTxnAccount(String currency) {
        return "AIP_TXN_" + currency;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //预付费卡主卡-额度
    public static String getPrepaidQuotaAccount(String currency) {
        return "PREPAID_QUOTA_" + currency;
    }

    //预付费卡主卡-发卡总额
    public static String getPrepaidSumAccount(String currency) {
        return "PREPAID_SUM_" + currency;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // 钱包主卡-发卡总额
    public static String getWalletSumAccount(String currency) {
        return "WALLET_SUM_" + currency;
    }

    // 钱包账户
    public static String getWalletAccount(String currency) {
        return "WALLET_" + currency;
    }
}