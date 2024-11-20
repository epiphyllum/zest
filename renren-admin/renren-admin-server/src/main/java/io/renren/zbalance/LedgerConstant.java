package io.renren.zbalance;

public class LedgerConstant {
    // 入金充值
    public static final int ORIGIN_TYPE_MONEY = 100;                          // 入金充值
    public static final int FACT_MONEY_IN_VA = 10001;                         // 1. 商户VA        +94HKD
    // 换汇
    public static final int ORIGIN_TYPE_EXCHANGE = 200;                       // 换汇
    public static final int FACT_EXCHANGE_FREEZE_VA = 20001;                  // 1. 冻结
    public static final int FACT_EXCHANGE_UNFREEZE_VA = 20002;                // 2. 解冻
    public static final int FACT_EXCHANGE_CONFIRM_VA = 20003;                 // 3. 确认  -100AUD
    public static final int FACT_EXCHANGE_IN_VA = 20004;                      // 4. 确认  +500HKD
    // 资金调拨-转入子商户VA
    public static final int ORIGIN_TYPE_ALLOCATE_M2S = 300;                   // 资金调拨-转入子商户VA
    public static final int FACT_M2S_IN_SUB_VA = 30001;                       // 1. 商户VA
    public static final int FACT_M2S_OUT_VA = 30002;                          // 2. 子商户VA
    // 资金调拨-转出子商户VA
    public static final int ORIGIN_TYPE_ALLOCATE_S2M = 400;                   // 资金调拨-转出子商户VA
    public static final int FACT_S2M_IN_VA = 40001;                           // 1. 子商户VA
    public static final int FACT_S2M_OUT_SUB_VA = 40002;                      // 2. 商户VA
    // 开卡
    public static final int ORIGIN_CARD_OPEN = 500;                           // 开卡
    public static final int FACT_CARD_OPEN_FREEZE_SUB_VA = 50000;             // 1. 子商户va冻结
    public static final int FACT_CARD_OPEN_UNFREEZE_SUB_VA = 50001;           // 2. 子商户va解冻
    public static final int FACT_CARD_OPEN_CONFIRM_SUB_VA = 50002;            // 3. 子商户va确认
    public static final int FACT_CARD_OPEN_IN_CARD_FEE = 50003;                // 4. 子商户-开卡费用账户
    public static final int FACT_CARD_OPEN_IN_AIP_CARD_FEE = 50004;           // 5. 通联开卡费
    // vpa共享子卡开卡
    public static final int ORIGIN_VPA_SHARE_OPEN = 501;                      // vpa共享子卡开卡
    public static final int FACT_VPA_SHARE_OPEN_FREEZE_SUB_VA = 50100;        // 1. 子商户va冻结
    public static final int FACT_VPA_SHARE_OPEN_UNFREEZE_SUB_VA = 50101;      // 2. 子商户va解冻
    public static final int FACT_VPA_SHARE_OPEN_CONFIRM_SUB_VA = 50102;       // 3. 子商户va确认
    public static final int FACT_VPA_SHARE_OPEN_IN_CARD_FEE = 50103;          // 4. 子商户费用账户+
    public static final int FACT_VPA_SHARE_OPEN_IN_AIP_CARD_FEE = 50104;      // 5. 通联开卡费
    // vpa预付费卡开卡
    public static final int ORIGIN_VPA_PREPAID_OPEN = 502;                    // vpa预付费子卡开卡
    public static final int FACT_VPA_PREPAID_FREEZE_SUB_VA = 50200;           // 1. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_UNFREEZE_SUB_VA = 50201;    // 2. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_CONFIRM_SUB_VA = 50202;     // 3. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_IN_SUB_FEE = 50203;         // 4. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_IN_AIP_CARD_FEE = 50204;    // 5. 通联开卡成本
    // 卡充值
    public static final int ORIGIN_TYPE_CARD_CHARGE = 600;                    // 卡充值
    public static final int FACT_CARD_CHARGE_FREEZE_SUB_VA = 60000;           // 0. 冻结va资金
    public static final int FACT_CARD_CHARGE_UNFREEZE_SUB_VA = 60001;         // 1. 解冻va资金
    public static final int FACT_CARD_CHARGE_CONFIRM_SUB_VA = 60002;          // 2. 确认va资金
    public static final int FACT_CARD_CHARGE_IN_CARD_SUM = 60003;             // 3. 子商户-卡汇总充值
    public static final int FACT_CARD_CHARGE_IN_DEPOSIT = 60004;              // 4. 子商户-保证金收取
    public static final int FACT_CARD_CHARGE_IN_CHARGE = 60005;               // 5. 子商户-保证金收取
    public static final int FACT_CARD_CHARGE_IN_AIP_DEPOSIT = 60006;          // 6. 通联累计保证金
    public static final int FACT_CARD_CHARGE_IN_AIP_CHARGE = 60007;           // 7. 通联累计充值手续费
    public static final int FACT_CARD_CHARGE_IN_AIP_CARD_SUM = 60008;         // 8. 通联累计手续费
    public static final int FACT_CARD_CHARGE_IN_PREPAID_MAIN = 60009;         // 9. 如果是预付费卡: 需要入金预付费主卡账户
    // 预付子卡费卡-批量开卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_OPEN_CHARGE = 601;                       // 预付费卡批量开卡卡充值
    public static final int FACT_PREPAID_OPEN_CHARGE_FREEZE_PREPAID_QUOTA = 60100;       // 1. 预付费主卡额度 冻结
    public static final int FACT_PREPAID_OPEN_CHARGE_UNFREEZE_PREPAID_QUOTA = 60101;     // 2. 预付费主卡额度 解冻
    public static final int FACT_PREPAID_OPEN_CHARGE_CONFIRM_PREPAID_QUOTA = 60102;      // 3. 预付费主卡额度 确认
    public static final int FACT_PREPAID_OPEN_CHARGE_IN_PREPAID_SUM = 60103;             // 4. 预付费卡发卡总额
    // 预付费卡-单卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_CHARGE = 602;                            // 预付费卡充值
    public static final int FACT_PREPAID_CHARGE_FREEZE_PREPAID_QUOTA = 60200;            // 1. 预付费主卡额度 冻结
    public static final int FACT_PREPAID_CHARGE_UNFREEZE_PREPAID_QUOTA = 60201;          // 2. 预付费主卡额度 解结.
    public static final int FACT_PREPAID_CHARGE_CONFIRM_PREPAID_QUOTA = 60202;           // 3. 预付费主卡额度 确认
    public static final int FACT_PREPAID_CHARGE_IN_PREPAID_SUM = 60203;                  // 4.  预付费卡发卡总额
    // 预付费卡提现: 操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_WITHDRAW = 603;                          // 预付费卡提现
    public static final int FACT_PREPAID_WITHDRAW_IN_PREPAID_QUOTA = 60300;              // 1: 调增主卡剩余额度
    public static final int FACT_PREPAID_WITHDRAW_OUT_PREPAID_SUM = 60301;               // 2: 发卡总额减
    // 卡提现
    public static final int ORIGIN_TYPE_CARD_WITHDRAW = 700;                             // 卡提现
    public static final int FACT_CARD_WITHDRAW_IN_SUB_VA = 70001;                        // 3. 子商户-VA+
    public static final int FACT_CARD_WITHDRAW_OUT_CARD_SUM = 70002;                 // 2. 子商户-卡汇总资金账户(确认成功)
    public static final int FACT_CARD_WITHDRAW_OUT_CARD_CHARGE = 70003;              // 2. 子商户-退手续费
    public static final int FACT_CARD_WITHDRAW_OUT_AIP_CHARGE = 70004;                   // 5. 通联-退手续费
    public static final int FACT_CARD_WITHDRAW_OUT_AIP_CARD_SUM = 70005;                 // 6. 通联-卡汇总资金
    public static final int FACT_CARD_WITHDRAW_OUT_PREPAID_QUOTA = 70006;                // 9. 如果是预防费主卡: 预付费主卡可以余额减少
    // 商户VA提现
    public static final int ORIGIN_TYPE_VA_WITHDRAW = 800;         // 商户VA提现
    // 释放商户担保金
    public static final int ORIGIN_TYPE_MFREE = 900;               // 释放担保金
    public static final int FACT_MFREE_OUT = 90001;                // 1. 商户保证账户 -100HKD
    public static final int FACT_MFREE_IN = 90002;                 // 2. 商户账户  +100HKD
    // 通联释放担保金
    public static final int ORIGIN_TYPE_FREE = 901;                // 通联释放担保金
    public static final int FACT_FREE_OUT = 90001;                 //
    public static final int FACT_FREE_IN = 90002;                  //
}