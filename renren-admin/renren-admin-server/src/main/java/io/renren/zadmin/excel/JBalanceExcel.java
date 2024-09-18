package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_balance
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JBalanceExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "归属方ID", index = 1)
    private Long deptId;
    @ExcelProperty(value = "归属方", index = 2)
    private String deptName;
    @ExcelProperty(value = "余额类型", index = 3)
    private String balanceType;
    @ExcelProperty(value = "余额名称", index = 4)
    private String balanceName;
    @ExcelProperty(value = "币种", index = 5)
    private String currency;
    @ExcelProperty(value = "余额", index = 6)
    private BigDecimal balance;
    @ExcelProperty(value = "冻结额", index = 7)
    private BigDecimal frozen;
    @ExcelProperty(value = "版本号", index = 8)
    private Integer version;
    @ExcelProperty(value = "创建者", index = 9)
    private Long creator;
    @ExcelProperty(value = "创建时间", index = 10)
    private Date createDate;
}