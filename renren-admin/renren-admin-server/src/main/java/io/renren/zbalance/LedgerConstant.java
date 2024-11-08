package io.renren.zbalance;

public class LedgerConstant {


    // 入金充值
    public static final int ORIGIN_TYPE_MONEY = 100;                 // 入金充值
    public static final int FACT_MONEY_VA = 10001;                   // 1. 商户VA        +94HKD
    public static final int FACT_MONEY_DEPOSIT = 10002;              // 2. 保证金预扣账户  +5HKD
    public static final int FACT_MONEY_CHARGE_FEE = 10003;           // 3. 手续费预扣账户  +1HKD
    // 换汇
    public static final int ORIGIN_TYPE_EXCHANGE = 200;                 // 换汇
    public static final int FACT_EXCHANGE_FREEZE_VA = 20001;            // 1. 冻结
    public static final int FACT_EXCHANGE_FREEZE_DEPOSIT = 20002;       // 1. 冻结
    public static final int FACT_EXCHANGE_FREEZE_CHARGE_FEE = 20003;    // 1. 冻结
    public static final int FACT_EXCHANGE_UN_FREEZE_VA = 20010;         // 2. 解冻
    public static final int FACT_EXCHANGE_UN_FREEZE_DEPOSIT = 20011;    // 2. 解冻
    public static final int FACT_EXCHANGE_UN_FREEZE_CHARGE_FEE = 20012; // 2. 解冻
    public static final int FACT_EXCHANGE_CONFIRM_VA = 20020;           // 3. 确认  -100AUD
    public static final int FACT_EXCHANGE_CONFIRM_DEPOSIT = 20021;      // 3. 确认  -100AUD
    public static final int FACT_EXCHANGE_CONFIRM_CHARGE_FEE = 20022;   // 3. 确认  -100AUD
    public static final int FACT_EXCHANGE_IN_VA = 20030;                // 4. 确认  +500HKD
    public static final int FACT_EXCHANGE_IN_DEPOSIT = 20031;           // 4. 记账商户账户  +500HKD
    public static final int FACT_EXCHANGE_IN_CHARGE_FEE = 20032;        // 4. 记账商户账户  +500HKD

    // 资金调拨-转入子商户VA
    public static final int ORIGIN_TYPE_ALLOCATE_M2S = 300;       // 资金调拨-转入子商户VA
    public static final int FACT_M2S_IN = 30001;                  // 1. 商户VA   -100HKD
    public static final int FACT_M2S_OUT = 30002;                 // 2. 子商户VA +100HKD
    // 资金调拨-转出子商户VA
    public static final int ORIGIN_TYPE_ALLOCATE_S2M = 400;       // 资金调拨-转出子商户VA
    public static final int FACT_S2M_IN = 40001;                  // 1. 子商户VA -100HKD
    public static final int FACT_S2M_OUT = 40002;                 // 2. 商户VA   +100HKD
    // 开卡
    public static final int ORIGIN_CARD_OPEN = 500;               // 开卡
    public static final int FACT_CARD_OPEN_FREEZE = 50000;        // 1. 子商户费用账户 冻结
    public static final int FACT_CARD_OPEN_UN_FREEZE = 50001;     // 2. 子商户费用账户 解冻
    public static final int FACT_CARD_OPEN_CONFIRM = 50002;       // 3. 子商户费用账户 确认
    public static final int FACT_CARD_OPEN_FEE_IN = 50003;        // 4. 子商户费用账户
    // vpa子卡开卡
    public static final int ORIGIN_VPA_OPEN = 501;                 // 开卡
    public static final int FACT_VPA_OPEN_FREEZE = 50100;          // 1. 子商户费用账户 冻结
    public static final int FACT_VPA_OPEN_UN_FREEZE = 50101;       // 2. 子商户费用账户 冻结
    public static final int FACT_VPA_OPEN_CONFIRM = 50102;         // 3. 子商户费用账户 冻结
    public static final int FACT_VPA_OPEN_FEE_IN = 50103;          // 4. 子商户费用账户 冻结

    // 卡充值
    public static final int ORIGIN_TYPE_CARD_CHARGE = 600;             // 卡充值
    public static final int FACT_CARD_CHARGE_FREEZE = 60000;           // 1. 子商户VA        -100HKD(冻结)
    public static final int FACT_CARD_CHARGE_UN_FREEZE = 60001;        // 2. 子商户VA        -100HKD(解冻)
    public static final int FACT_CARD_CHARGE_CONFIRM = 60002;          // 3. 子商户VA        -100HKD(确认成功)
    public static final int FACT_CARD_CHARGE_IN = 60003;               // 4. 子商户卡资金账户  +100HKD(卡汇总增加)
    public static final int FACT_CARD_CHARGE_IN_PREPAID_MAIN = 60004;  // 5. 如果是给预付费主卡充值， 需要增加预付费主卡可分配额

    // 预付费卡-批量开卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_OPEN_CHARGE = 601;       // 预付费卡充值
    public static final int FACT_PREPAID_OPEN_CHARGE_FREEZE = 60100;     // 1.
    public static final int FACT_PREPAID_OPEN_CHARGE_UN_FREEZE = 60101;  // 1.
    public static final int FACT_PREPAID_OPEN_CHARGE_CONFIRM = 60102;    // 1.
    public static final int FACT_PREPAID_OPEN_CHARGE_IN = 60103;         // 1.

    // 预付费卡-单卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_CHARGE = 602;       // 预付费卡充值
    public static final int FACT_PREPAID_CHARGE_FREEZE = 60200;     // 1.
    public static final int FACT_PREPAID_CHARGE_UN_FREEZE = 60201;  // 1.
    public static final int FACT_PREPAID_CHARGE_CONFIRM = 60202;    // 1.
    public static final int FACT_PREPAID_CHARGE_IN = 60203;         // 1.

    // 预付费卡提现: 操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_WITHDRAW = 603;     // 预付费卡提现
    public static final int FACT_PREPAID_WITHDRAW_UP = 60300;       // 1: 调增主卡剩余额度

    // 卡提现
    public static final int ORIGIN_TYPE_CARD_WITHDRAW = 700;               // 卡提现
    public static final int FACT_CARD_WITHDRAW_FREEZE = 70000;             // 0. 子商户卡资金账户 -100HKD(冻结)
    public static final int FACT_CARD_WITHDRAW_UN_FREEZE = 70001;          // 1. 子商户卡资金账户 -100HKD(解冻)
    public static final int FACT_CARD_WITHDRAW_CONFIRM = 70002;            // 2. 子商户卡资金账户 -100HKD(确认成功)
    public static final int FACT_CARD_WITHDRAW_IN = 70003;                 // 3. 子商户VA       +100HKD(va退回)
    public static final int FACT_CARD_WITHDRAW_OUT_PREPAID_MAIN = 70004;   // 4. 如果是预防费主卡: 预付费主卡可以余额减少

    // 商户VA提现
    public static final int ORIGIN_TYPE_VA_WITHDRAW = 800;         // 商户VA提现

    // 释放商户担保金
    public static final int ORIGIN_TYPE_MFREE = 900;               // 释放担保金
    public static final int FACT_MFREE_OUT = 90001;                // 1. 商户保证账户 -100HKD
    public static final int FACT_MFREE_IN = 90002;                 // 2. 商户账户  +100HKD
}