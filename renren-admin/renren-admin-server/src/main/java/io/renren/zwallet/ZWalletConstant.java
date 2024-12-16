package io.renren.zwallet;

public class ZWalletConstant {
    // 钱包交易: 给钱包充值, 给钱包提现, 账户升级
    public static final String WALLET_TXN_CHARGE = "charge";                     // 钱包充值
    public static final String WALLET_TXN_WITHDRAW = "withdraw";                 // 钱包提现
    public static final String WALLET_TXN_SWAP = "swap";                         // 钱包兑换 usdt--> HKD, USDT
    public static final String WALLET_TXN_EXCHANGE = "exchange";                 // 换汇
    public static final String WALLET_TXN_APPLY = "apply";                       // 卡片申请
    public static final String WALLET_TXN_WITHDRAW_CARD = "withdrawCard";        // 卡提现
    public static final String WALLET_TXN_CHARGE_CARD = "chargeCard";            // 卡充值
    public static final String WALLET_TXN_MONTH_FEE = "monthFee";                // 月费扣除
    public static final String WALLET_TXN_UPGRADE = "upgrade";                   // 账户升级
    public static final String WALLET_TXN_VERIFY = "verify";                     // 实名认证费
    public static final String WALLET_TXN_COMMISSION_OPEN = "commissionOpen";    // 开卡返佣
    public static final String WALLET_TXN_COMMISSION_CHARGE = "commissionCharge";// 充值返佣
    // 钱包交易状态
    public static final String WALLET_TXN_STATUS_NEW = "00";                // 新建
    public static final String WALLET_TXN_STATUS_SUCCESS = "01";            // 成功
    public static final String WALLET_TXN_STATUS_FAIL = "02";               // 失败
    // 钱包账户等级:  hkd_level, usd_level
    public static final String WALLET_LEVEL_BASIC = "basic";
    public static final String WALLET_LEVEL_PREMIUM = "premium";
}
