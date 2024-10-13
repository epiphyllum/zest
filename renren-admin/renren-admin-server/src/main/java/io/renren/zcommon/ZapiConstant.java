package io.renren.zcommon;

// 接口名称
public class ZapiConstant {
    // 子商户服务(2)
    public static final String API_subCreate = "subCreate";                  // 创建子商户
    public static final String API_subQuery = "subQuery";                    // 查询子商户
    public static final String API_subNotify = "subNotify";                  // 通知创建子商户状态
    // 账户服务(4): 来账白名单, 商户+子商户账户管理
    public static final String API_moneyAccountAdd = "moneyAccountAdd";      // 添加来账账户白名单
    public static final String API_moneyAccountNotify = "moneyAccountNotify";// 通知
    public static final String API_moneyAccountQuery = "moneyAccountQuery";  // 查询来账账户白名单
    public static final String API_vaAccountQuery = "vaAccountQuery";        // 商户账户查询
    public static final String API_vaSubAccountQuery = "vaSubAccountQuery";  // 子商户账户查询
    // 资金服务(8): 入金, 资金调度
    public static final String API_moneyApply = "moneyApply";                // 入金申请
    public static final String API_moneyConfirm = "moneyConfirm";            // 入金申请确认
    public static final String API_moneyMaterial = "moneyMaterial";          // 提交证明材料
    public static final String API_moneyNotify = "moneyNotify";              // 入金通知
    public static final String API_m2s = "m2s";                              // 转入子商户
    public static final String API_s2m = "s2m";                              // 子商户转出
    public static final String API_m2sQuery = "ms2Query";                    // 转入子商户查询
    public static final String API_s2mQuery = "s2mQuery";                    // 转出子商户查询
    // 卡申请服务(4)
    public static final String API_cardNew = "cardNew";                      // 开卡
    public static final String API_cardNewQuery = "cardNewQuery";            // 开卡查询
    public static final String API_cardNewNotify = "cardNewNotify";          // 开卡通知
    public static final String API_cardNewActivate = "cardNewActivate";      // 卡激活
    // 卡状态变更服务(5)
    public static final String API_cardChange = "cardChange";                // 卡状态服务
    public static final String API_cardChangeQuery = "cardChangeQuery";      // 卡状态查询
    public static final String API_cardChangeNotify = "cardChangeNotify";    // 卡状态通知
    public static final String API_cardBalance = "cardBalance";              // 卡余额查询
    public static final String API_cardPayInfo = "cardPayInfo";              // 卡支付信息: cvv2,expiredate, 目前不外提供
    // 卡资金管理与卡交易(8)
    public static final String API_cardCharge = "cardCharge";                // 卡充值
    public static final String API_cardChargeQuery = "cardChargeQuery";      // 卡充值查询
    public static final String API_cardChargeNotify = "cardChargeNotify";    // 卡充值
    public static final String API_cardWithdraw = "cardWithdraw";            // 卡提现
    public static final String API_cardWithdrawQuery = "cardWithdrawQuery";  // 卡提现查询
    public static final String API_cardWithdrawNotify = "cardWithdrawNotify";// 卡提现通知
    public static final String API_cardTxnNotify = "cardTxnNotify";          // 卡交易流水通知
    public static final String API_cardTxnDownload = "cardTxnDownload";      // 卡结算交易下载
    // 换汇服务(4)
    public static final String API_exchange = "exchange";                    // 换汇申请
    public static final String API_exchangeLock = "exchangeLock";            // 锁汇询价
    public static final String API_exchangeConfirm = "exchangeConfirm";      // 换汇申请单确认
    public static final String API_exchangeQuery = "exchangeQuery";          // 换汇申请单查询
    // 文件服务(1): 上传文件
    public static final String API_upload = "upload";                        // 上传文件
}


