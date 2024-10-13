package io.renren.zin.cardmoney.dto;

import lombok.Data;

import java.math.BigDecimal;

// 3100 - 缴纳保证金
@Data
public class TDepositRequest {
    String meraplid;        //申请单流水
    String cardno;          //卡号
    String payerid;         //出账账户 VA账户列表查询 响应报文的账户唯一标识id
    BigDecimal amount;      //缴纳金额
    String payeeaccount;    //交易对手
    String procurecontent;  //采购内容
    String agmfid;          //保证金对应合同协议
}









