package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JWalletConfigExcel {
    @ExcelProperty(value = "代理", index = 0)
    private String agentName;
    @ExcelProperty(value = "商户", index = 1)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 2)
    private String subName;
    @ExcelProperty(value = "用户充值手续费率", index = 3)
    private BigDecimal chargeRate;
    @ExcelProperty(value = "最小充值港币", index = 4)
    private BigDecimal minHkd;
    @ExcelProperty(value = "最小充值美金", index = 5)
    private BigDecimal minUsd;
    @ExcelProperty(value = "最小充值u", index = 6)
    private BigDecimal minUsdt;
}