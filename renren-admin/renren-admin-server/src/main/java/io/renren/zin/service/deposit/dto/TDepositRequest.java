package io.renren.zin.service.deposit.dto;

import lombok.Data;

import java.math.BigDecimal;

// 缴纳保证金
@Data
public class TDepositRequest {
    String meraplid;        //申请单流水 String	32	Y	 客户自己生成，保持唯一
    String cardno;            //卡号 String	30	Y
    String payerid;            //出账账户 String	30	Y	【VA账户列表查询】响应报文的账户唯一标识id
    BigDecimal amount;            //缴纳金额 Number	18,2	Y
    String payeeaccount;    //交易对手 String	30	O
    String procurecontent;    //采购内容 String	200	O
    String agmfid;            //保证金对应合同协议 String	100	O
}









