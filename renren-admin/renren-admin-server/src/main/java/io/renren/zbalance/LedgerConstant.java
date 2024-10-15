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
    // 卡充值
    public static final int ORIGIN_TYPE_CARD_CHARGE = 600;        // 卡充值
    public static final int FACT_CARD_CHARGE_FREEZE = 60000;      // 1. 子商户VA        -100HKD(冻结)
    public static final int FACT_CARD_CHARGE_UN_FREEZE = 60001;   // 2. 子商户VA        -100HKD(解冻)
    public static final int FACT_CARD_CHARGE_CONFIRM = 60002;     // 3. 子商户VA        -100HKD(确认成功)
    public static final int FACT_CARD_CHARGE_IN = 60003;          // 4. 子商户卡资金账户  +100HKD(卡汇总增加)
    // 卡提现
    public static final int ORIGIN_TYPE_CARD_WITHDRAW = 700;       // 卡提现
    public static final int FACT_CARD_WITHDRAW_FREEZE = 70000;     // 0. 子商户卡资金账户 -100HKD(冻结)
    public static final int FACT_CARD_WITHDRAW_UN_FREEZE = 70001;  // 1. 子商户卡资金账户 -100HKD(解冻)
    public static final int FACT_CARD_WITHDRAW_CONFIRM = 70002;    // 2. 子商户卡资金账户 -100HKD(确认成功)
    public static final int FACT_CARD_WITHDRAW_IN = 70003;         // 3. 子商户VA       +100HKD(va退回)
    // 商户VA提现
    public static final int ORIGIN_TYPE_VA_WITHDRAW = 800;         // 商户VA提现
    // todo

}