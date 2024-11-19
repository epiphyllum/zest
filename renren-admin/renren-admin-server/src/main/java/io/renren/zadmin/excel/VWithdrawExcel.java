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
public class VWithdrawExcel {
    @ExcelProperty(value = "提现额", index = 0)
    private BigDecimal cardSum;
    @ExcelProperty(value = "平台手续费", index = 1)
    private BigDecimal aipCharge;
    @ExcelProperty(value = "手续费", index = 2)
    private BigDecimal charge;
    @ExcelProperty(value = "代理名", index = 3)
    private String agentName;
    @ExcelProperty(value = "商户名", index = 4)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 5)
    private String subName;
    @ExcelProperty(value = "币种", index = 6)
    private String currency;
    @ExcelProperty(value = "产品", index = 7)
    private String marketproduct;
    @ExcelProperty(value = "日期", index = 8)
    private Date statDate;
}