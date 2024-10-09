package io.renren.zin.config;

import java.util.HashMap;
import java.util.Map;

public class ZinConstant {

    // 通用状态管理
    public static final int STATE_PROCESSING = 0; // 处理中
    public static final int STATE_MANUAL = 1;     // 需要介入
    public static final int STATE_SUCCESS = 2;    // 成功
    public static final int STATE_FAIL = 3;       // 失败

    // 支付申请单状态
    public static final Map<String, Integer> payApplyStateMap = new HashMap<>() {
        {
            // 处理中
            put("01", STATE_PROCESSING);  // 处理中提交申报明细成功后的状态，此时需要运营人员初审通过。
            put("05", STATE_PROCESSING);  // 交易处理渠道处理中间过程。
            put("16", STATE_PROCESSING);  // 初审通过后的状态，此时需要运营人员复审通过。
            put("12", STATE_PROCESSING);  // 入金待收款环节。
            put("23", STATE_PROCESSING);  // 入金已退款
            put("38", STATE_PROCESSING);  // 待补充材料
            // 需要操作
            put("13", STATE_MANUAL);      // 待确认提交申请单后，未确认。
            // 终态成功
            put("06", STATE_SUCCESS);     // 处理成功申请单全流程成功结束。
            // 终态失败
            put("02", STATE_FAIL);        // 审核不通过通联方运营人员审核申请单不通过。
            put("07", STATE_FAIL);        // 处理失败申请单处理失败，具体原因根据提示了解。
            put("11", STATE_FAIL);        // 交易已关闭申请单流程中登录商户平台手动关闭，或由运营人员手动关闭申请单。

            ////////////////////////////////////////////////////////////////
            put("NA", STATE_PROCESSING);  // dj增加状态
            put("CF", STATE_PROCESSING);  // dj增加状态
            put("CC", STATE_FAIL);        // dj增加状态
        }
    };

    // 主体类型:
    public static final String BELONG_TYPE_EMPLOYEE = "1";  // 员工
    public static final String BELONG_TYPE_COOP = "2";      // 合作企业

    // 卡产品类型
    public static final String CARD_PRODUCT_ALL = "001001";         // 通华金服VISA公务卡 -- 虚实同发
    public static final String CARD_PRODUCT_VIRTUAL = " 001201";    // 通华金服VISA虚拟卡 -- 虚拟卡

    // 卡类型
    public static final String CARD_TYPE_VIRTUAL = "1";      // 虚拟卡
    public static final String CARD_TYPE_BOTH = "4";         // 虚实同发
    public static final Map<String, String> cardMap = new HashMap<>() {{
        put(CARD_TYPE_VIRTUAL, CARD_PRODUCT_VIRTUAL);
        put(CARD_TYPE_BOTH, CARD_PRODUCT_ALL);
    }};

    // 持卡人身份
    public static final String CARD_HOLDER_TYPE_LEGAL = "1";        // 法人持有
    public static final String CARD_HOLDER_TYPE_OTHER = "0";        // 其他管理员

    // 卡申请单状态
    public static final String CARD_APPLY_NEW_DJ = "00";               // 新建卡的状态的状态: 我方新增状态
    public static final String CARD_APPLY_TO_VERIFY = "01";         //
    public static final String CARD_APPLY_VERIFY_FAIL = "02";       //
    public static final String CARD_APPLY_PROCESSING = "03";        //
    public static final String CARD_APPLY_SUCCESS = "04";           //
    public static final String CARD_APPLY_FAIL = "05";              //
    public static final String CARD_APPLY_REFUND = "06";            //
    public static final String CARD_APPLY_CLOSE = "07";             //
    public static final String CARD_APPLY_TO_VERIFY_AGAIN = "08";   //

    public static boolean isCardApplyFail(String state) {
        return state.equals(CARD_APPLY_VERIFY_FAIL) ||
                state.equals(CARD_APPLY_FAIL) ||
                state.equals(CARD_APPLY_CLOSE);
    }

    public static boolean isCardApplySuccess(String state) {
        return state.equals(CARD_APPLY_SUCCESS);
    }

    // 卡状态
    public static final String CARD_STATE_NEW_DJ = "00";               // 开卡成功后的初始状态:  我方添加的
    public static final String CARD_STATE_TO_VERIFY = "01";         // 待审核
    public static final String CARD_STATE_PROCESSING = "02";        // 申请处理中
    public static final String CARD_STATE_SUCCESS = "03";           // 申请成功（正常）
    public static final String CARD_STATE_FAIL = "04";              // 申请失败
    public static final String CARD_STATE_CANCELLED = "05";         // 已销卡
    public static final String CARD_STATE_FREEZE = "06";            // 止付（冻结）
    public static final String CARD_STATE_FREEZE_OP = "07";         // 运营冻结
    public static final String CARD_STATE_CANCELLING = "08";        // 销卡处理中
    public static final String CARD_STATE_LOSS = "09";              // 挂失
    public static final String CARD_STATE_EXPIRE = "10";            // 失效
    public static final String CARD_STATE_CANCELLING_VERIFY = "11"; // 销卡中（待审核）
    public static final String CARD_STATE_TO_MAKE = "12";           // 待制卡
    public static final String CARD_STATE_TO_DELIVER = "13";        // 待寄出
    public static final String CARD_STATE_TO_ACTIVATE = "14";       // 待激活

    // 支付交易类型
    public static final String CP201 = "CP201";                    // 汇款充值 | 外部资金汇入
    public static final String CP213 = "CP213";                    // 形账户入账
    public static final String CP220 = "CP220";                    // 金退款	入金审核不通过，资金退回
    public static final String CP109 = "CP109";                    // 岸下发	资金提现
    public static final String CP211 = "CP211";                    // 岸换汇	充值其他币种的资金，可以兑换成HKD

    // 卡申请交易类型
    public static final String CP450 = "CP450";                    // 卡	申请开卡
    public static final String CP453 = "CP453";                    // 销	卡的注销/撤回注销
    public static final String CP458 = "CP458";                    // 销撤回
    public static final String CP451 = "CP451";                    // 证金缴纳	卡片资金充值
    public static final String CP452 = "CP452";                    // 证金提现	卡片资金提现
    public static final String CP462 = "CP462";                    // 放担保金

}
