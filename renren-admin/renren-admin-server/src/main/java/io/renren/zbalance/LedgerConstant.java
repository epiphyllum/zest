package io.renren.zbalance;

public class LedgerConstant {
    // 原始凭证(100):  收到商户来账 100AUD
    public static final int IN_MONEY = 10001;            // 1. 记账商户账户 +100AUD

    // 原始凭证(200):  换汇
    public static final int EXCHANGE_FREEZE = 20000;
    public static final int EXCHANGE_UN_FREEZE = 20001;
    public static final int EXCHANGE_CONFIRM = 20002;        // 2. 记账商户账户  -100AUD
    public static final int EXCHANGE_IN = 20003;         // 1. 记账商户账户  +500HKD


    // 原始凭证(300): i2v
    public static final int I2V_OUT = 30001;             // 1. 商户入金 -100HKD
    public static final int I2V_IN = 30002;              // 2. 商户VA  +94HKD
    public static final int I2V_IN_DEPOSIT = 30003;      // 2. 商户保证金预扣账户  +5HKD
    public static final int I2v_IN_CHARGE_FEE = 30004;   // 3. 商户手续费预扣账户  +1HKD
    // 原始凭证(301): v2i:
    public static final int V2I_OUT = 30101;             // 1. 商户VA -94
    public static final int V2I_IN = 30102;              // 4. 入金户 +100
    public static final int V2I_OUT_CHARGE_FEE = 30103;  // 3. 商户手续费预扣账户  -1
    public static final int V2I_OUT_DEPOSIT = 30104;     // 2. 商户保证金预扣账户  : 保证金不退
    // 原始凭证(400): m2s
    public static final int M2S_IN = 40001;              // 1. 商户VA   -100HKD
    public static final int M2S_OUT = 40002;             // 2. 子商户VA +100HKD
    // 原始凭证(401): s2m
    public static final int S2M_IN = 40101;              // 1. 子商户VA -100HKD
    public static final int S2M_OUT = 40102;             // 2. 商户VA   +100HKD

    // 卡充值冻结
    public static final int CARD_CHARGE_FREEZE = 50000;     // 1. 子商户VA        -100HKD
    public static final int CARD_CHARGE_UN_FREEZE = 50001;     // 1. 子商户VA        -100HKD
    // 原始凭证(500): cardCharge
    public static final int CARD_CHARGE_CONFIRM = 50002;     // 1. 子商户VA        -100HKD
    public static final int CARD_CHARGE_IN = 50003;      // 2. 子商户卡资金账户  +100HKD

    // 原始凭证(501): cardWithdraw
    public static final int CARD_WITHDRAW_FREEZE = 50100;   // 1. 子商户卡资金账户 -100HKD
    public static final int CARD_WITHDRAW_UN_FREEZE = 50101;   // 1. 子商户卡资金账户 -100HKD
    public static final int CARD_WITHDRAW_CONFIRM = 50102;   // 1. 子商户卡资金账户 -100HKD
    public static final int CARD_WITHDRAW_IN = 50104;    // 2. 子商户VA       +100HKD


    // 开卡
    public static final int CARD_OPEN_FREEZE = 60100;    // 2. 子商户费用账户  +100HKD
    public static final int CARD_OPEN_UN_FREEZE = 60101;    // 2. 子商户费用账户  +100HKD
    public static final int CARD_OPEN_CONFIRM = 60102;    // 2. 子商户费用账户  +100HKD
    public static final int CARD_OPEN_FEE_IN = 60103;    // 2. 子商户费用账户  +100HKD


}