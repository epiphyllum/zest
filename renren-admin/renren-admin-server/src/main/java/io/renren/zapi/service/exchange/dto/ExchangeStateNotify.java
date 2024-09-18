package io.renren.zapi.service.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeStateNotify {
    private String meraplid;    //申请单流水
    private String groupId;     //   批次号
    private String accid;    // 受益人ID
    private String acctno;    // 受益人账号
    private String lockflg;//	锁定方
    private BigDecimal payamount;//	支付金额
    private BigDecimal stlamount;//	结算金额
    private BigDecimal fee;//手续费
    private BigDecimal exchrate;//	汇率
    private String payccy;//	支付币种
    private String stlccy;//	结算币种
    private String declarecode;//	申报编号
    private String ps;//	附言
    private String purpose;//	汇款用途
    private String remark;//	备注
    private String applyid;//	申请单号
    private String state;//	状态
    private String stateExplain;//	状态描述
    private String time;//	通知时间  ISO格式[yyyy-MM-dd'T'HH:mm:ssZ]
}
