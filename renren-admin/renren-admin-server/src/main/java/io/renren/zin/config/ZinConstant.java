package io.renren.zin.config;

import java.util.HashMap;
import java.util.Map;

public class ZinConstant {

    // 卡申请单状态
    public static final String CARD_STATE_NEW = "00";
    public static final String CARD_STATE_TO_VERIFY = "01";
    public static final String CARD_STATE_APPLY_PROCESSING = "02";
    public static final String CARD_STATE_APPLY_SUCCESS = "03";
    public static final String CARD_STATE_APPLY_FAIL = "04";
    public static final String CARD_STATE_CANCELLED = "05";
    public static final String CARD_STATE_FREEZE = "06";
    public static final String CARD_STATE_FREEZE_OP = "07";
    public static final String CARD_STATE_CANCEL_PROCESSING = "08";
    public static final String CARD_STATE_LOSS = "09";
    public static final String CARD_STATE_EXPIRE = "10";

    //
    public static final String CARD_PRODUCT_ALL     = "001001";    // 通华金服VISA公务卡 -- 虚实同发
    public static final String CARD_PRODUCT_VIRTUAL =" 001201";    // 通华金服VISA虚拟卡 -- 虚拟卡

    //
    public static final String CARD_TYPE_VIRTUAL = "1";  // 虚拟卡
    public static final String CARD_TYPE_ALL = "4"; // 虚实同发
    public static final Map<String, String> cardMap = new HashMap<>() {{
        put(CARD_TYPE_VIRTUAL, CARD_PRODUCT_VIRTUAL);
        put(CARD_TYPE_ALL, CARD_PRODUCT_ALL);
    }};

    // 交易类型
    public static final String CP201  =  "CP201"; // 汇款充值	外部资金汇入
    public static final String CP213  =  "CP213"; // 形账户入账
    public static final String CP220  =  "CP220"; // 金退款	入金审核不通过，资金退回
    public static final String CP109  =  "CP109"; // 岸下发	资金提现
    public static final String CP211  =  "CP211"; // 岸换汇	充值其他币种的资金，可以兑换成HKD
    public static final String CP450  =  "CP450"; // 卡	申请开卡
    public static final String CP453  =  "CP453"; // 销	卡的注销/撤回注销
    public static final String CP458  =  "CP458"; // 销撤回
    public static final String CP451  =  "CP451"; // 证金缴纳	卡片资金充值
    public static final String CP452  =  "CP452"; // 证金提现	卡片资金提现
    public static final String CP462  =  "CP462"; // 放担保金


}
