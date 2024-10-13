package io.renren.zin.cardmoney.dto;

import io.renren.zin.TResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TCardBondResponse extends TResult {
    @Data
    public static class Item {
        String meraplid;               // 申请单流水
        String applyid;                // 申请单号
        String cardno;                 // 卡号
        String trxcode;                // 交易类型 CP451：保证金缴纳CP452：保证金提现
        String state;                  // 申请单状态
        BigDecimal amount;             // 申请金额
        BigDecimal entryamount;        // 入账金额
        String currency;               // 币种
        BigDecimal fee;                // 手续费
        String feecurrency;            // 手续费币种
        BigDecimal securityamount;     // 担保金金额
        BigDecimal securitycurrency;   // 担保金币种
        String createtime;             // 申请时间
    }
    List<Item> applylist;
}
