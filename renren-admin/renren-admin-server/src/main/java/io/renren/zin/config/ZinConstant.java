package io.renren.zin.config;

import java.util.HashMap;
import java.util.Map;

public class ZinConstant {

    // 卡申请单状态
    public static final String CARD_APPLY_NEW = "00";
    public static final String CARD_APPLY_TO_VERIFY = "01";
    public static final String CARD_APPLY_VERIFY_FAIL = "02";
    public static final String CARD_APPLY_PROCESSING = "03";
    public static final String CARD_APPLY_SUCCESS = "04";
    public static final String CARD_APPLY_FAIL = "05";
    public static final String CARD_APPLY_REFUND = "06";
    public static final String CARD_APPLY_CLOSE = "07";
    public static final String CARD_APPLY_TO_VERIFY_AGAIN = "08";

    // 卡状态
    public static final String CARD_STATE_TO_VERIFY = "01"; // 待审核
    public static final String CARD_STATE_PROCESSING = "02"; // 申请处理中
    public static final String CARD_STATE_SUCCESS = "03"; // 申请成功（正常）
    public static final String CARD_STATE_FAIL = "04"; // 申请失败
    public static final String CARD_STATE_CANCELLED = "05"; // 已销卡
    public static final String CARD_STATE_FREEZE = "06"; // 止付（冻结）
    public static final String CARD_STATE_FREEZE_OP = "07"; // 运营冻结
    public static final String CARD_STATE_CANCELLING = "08"; // 销卡处理中
    public static final String CARD_STATE_LOSS = "09"; // 挂失
    public static final String CARD_STATE_EXPIRE = "10"; // 失效
    public static final String CARD_STATE_CANCELLING_VERIFY = "11"; // 销卡中（待审核）
    public static final String CARD_STATE_TO_MAKE = "12"; // 待制卡
    public static final String CARD_STATE_TO_DELIVER = "13"; // 待寄出
    public static final String CARD_STATE_TO_ACTIVATE = "14"; // 待激活

    // 卡产品类型
    public static final String CARD_PRODUCT_ALL = "001001";    // 通华金服VISA公务卡 -- 虚实同发
    public static final String CARD_PRODUCT_VIRTUAL = " 001201";    // 通华金服VISA虚拟卡 -- 虚拟卡

    // 卡类
    public static final String CARD_TYPE_VIRTUAL = "1";  // 虚拟卡
    public static final String CARD_TYPE_ALL = "4"; // 虚实同发
    public static final Map<String, String> cardMap = new HashMap<>() {{
        put(CARD_TYPE_VIRTUAL, CARD_PRODUCT_VIRTUAL);
        put(CARD_TYPE_ALL, CARD_PRODUCT_ALL);
    }};

    // 交易类型
    public static final String CP201 = "CP201"; // 汇款充值	外部资金汇入
    public static final String CP213 = "CP213"; // 形账户入账
    public static final String CP220 = "CP220"; // 金退款	入金审核不通过，资金退回
    public static final String CP109 = "CP109"; // 岸下发	资金提现
    public static final String CP211 = "CP211"; // 岸换汇	充值其他币种的资金，可以兑换成HKD
    public static final String CP450 = "CP450"; // 卡	申请开卡
    public static final String CP453 = "CP453"; // 销	卡的注销/撤回注销
    public static final String CP458 = "CP458"; // 销撤回
    public static final String CP451 = "CP451"; // 证金缴纳	卡片资金充值
    public static final String CP452 = "CP452"; // 证金提现	卡片资金提现
    public static final String CP462 = "CP462"; // 放担保金

}
