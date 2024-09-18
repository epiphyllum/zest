package io.renren.zin.service.auth.dto;

import lombok.Data;

import java.math.BigDecimal;

// 授权交易通知
@Data
public class TAuthTxnNotify {
    private String logkv; //	流水号
    private String trxtype; //	交易类型
    private String cardno; //	卡号
    private String state; //	状态
    private String stateexplain; //	状态说明
    private BigDecimal amount; //	金额
    private String currency; //	币种
    private String trxtime; //	交易时间
    private String trxdir; //	交易方向
    private String trxaddr; //	交易地点
    private String authcode; //	授权码
    private String mcc; //	商户类别代码
    private String time; //	通知时间
}
