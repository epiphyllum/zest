package io.renren.zin.exchange.dto;


import lombok.Data;

// 2002 - 换汇申请单确认请求
@Data
public class TExchangeConfirmRequest {
   private String meraplid; // 申请单流水
   private String applyid;  // 申请单号
   private String extype;   // 成交方式
}
