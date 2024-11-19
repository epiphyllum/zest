package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_stat
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JStatExcel {
    @ExcelProperty(value = "代理", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户", index = 2)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 3)
    private String subName;
    @ExcelProperty(value = "币种", index = 4)
    private String currency;
    @ExcelProperty(value = "产品类型", index = 5)
    private String marketproduct;
    @ExcelProperty(value = "统计日期", index = 6)
    private Date statDate;
    @ExcelProperty(value = "充值总额", index = 7)
    private BigDecimal cardSum;
    @ExcelProperty(value = "充值手续费", index = 8)
    private BigDecimal charge;
    @ExcelProperty(value = "保证金", index = 9)
    private BigDecimal deposit;
    @ExcelProperty(value = "充值手续费-成本", index = 10)
    private BigDecimal aipCharge;
    @ExcelProperty(value = "保证金-成本", index = 11)
    private BigDecimal aipDeposit;
    @ExcelProperty(value = "提现", index = 12)
    private BigDecimal withdraw;
    @ExcelProperty(value = "提现手续费", index = 13)
    private BigDecimal withdrawCharge;
    @ExcelProperty(value = "提现成本", index = 14)
    private BigDecimal aipWithdrawCharge;
    @ExcelProperty(value = "发卡量", index = 15)
    private Long totalCard;
    @ExcelProperty(value = "开卡费用", index = 16)
    private BigDecimal cardFee;
    @ExcelProperty(value = "开卡成本", index = 17)
    private BigDecimal aipCardFee;
}