package io.renren.zapi.service.exchange.dto;


import lombok.Data;

@Data
public class ExchangeConfirmReq {
   private String meraplid; // 申请单流水
   private String applyid;  // 申请单号
   private String extype;  // 成交方式
}
