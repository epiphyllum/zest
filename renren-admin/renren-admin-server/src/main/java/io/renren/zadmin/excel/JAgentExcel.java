package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_agent
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.0 2024-08-16
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JAgentExcel {
    @ExcelProperty(value = "代理名称", index = 0)
    private String agentName;
    @ExcelProperty(value = "充值费率", index = 1)
    private BigDecimal chargeRate;
    @ExcelProperty(value = "一档金额", index = 2)
    private BigDecimal firstLimit;
    @ExcelProperty(value = "一档费率", index = 3)
    private BigDecimal firstRate;
    @ExcelProperty(value = "二挡金额", index = 4)
    private BigDecimal secondLimit;
    @ExcelProperty(value = "二挡费率", index = 5)
    private BigDecimal secondRate;
    @ExcelProperty(value = "三挡金额", index = 6)
    private BigDecimal thirdLimit;
    @ExcelProperty(value = "三挡费率", index = 7)
    private BigDecimal thirdRate;
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createDate;
}