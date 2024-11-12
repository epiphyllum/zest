package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_fee_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-12
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JFeeConfigExcel {
    @ExcelProperty(value = "商户名称", index = 0)
    private String merchantName;
    @ExcelProperty(value = "产品", index = 1)
    private String marketproduct;
    @ExcelProperty(value = "保证金扣率-成本", index = 2)
    private BigDecimal costDepositRate;
    @ExcelProperty(value = "手续费扣率-成本", index = 3)
    private BigDecimal costChargeRate;
    @ExcelProperty(value = "小金额手续费每笔", index = 4)
    private BigDecimal costL50;
    @ExcelProperty(value = "&gt;=50失败手续费", index = 5)
    private BigDecimal costGef50;
    @ExcelProperty(value = "失败费/笔, 当失败率&gt;15%", index = 6)
    private BigDecimal costFailFee;
    @ExcelProperty(value = "争议处理费", index = 7)
    private BigDecimal costDisputeFee;
    @ExcelProperty(value = "保证金扣率", index = 8)
    private BigDecimal depositRate;
    @ExcelProperty(value = "手续费扣率", index = 9)
    private BigDecimal chargeRate;
    @ExcelProperty(value = "小金额手续费每笔", index = 10)
    private BigDecimal l50;
    @ExcelProperty(value = "&gt;=50 fail 手续费", index = 11)
    private BigDecimal gef50;
    @ExcelProperty(value = "失败费/笔, faiL_rate &gt; 15%", index = 12)
    private BigDecimal failFee;
    @ExcelProperty(value = "争议处理费", index = 13)
    private BigDecimal disputeFee;
}