package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * VIEW
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class VDepositExcel {
    @ExcelProperty(value = "BigDecimal", index = 0)
    private BigDecimal charge;
    @ExcelProperty(value = "BigDecimal", index = 1)
    private BigDecimal deposit;
    @ExcelProperty(value = "BigDecimal", index = 2)
    private BigDecimal aipCharge;
    @ExcelProperty(value = "BigDecimal", index = 3)
    private BigDecimal aipDeposit;
    @ExcelProperty(value = "BigDecimal", index = 4)
    private BigDecimal cardSum;
    @ExcelProperty(value = "BigDecimal", index = 5)
    private BigDecimal aipCardSum;
    @ExcelProperty(value = "Integer", index = 6)
    private Integer id;
    @ExcelProperty(value = "代理名", index = 7)
    private String agentName;
    @ExcelProperty(value = "商户名", index = 8)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 9)
    private String subName;
    @ExcelProperty(value = "String", index = 10)
    private String currency;
    @ExcelProperty(value = "String", index = 11)
    private String marketproduct;
    @ExcelProperty(value = "Date", index = 12)
    private Date statDate;
}