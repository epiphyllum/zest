package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_inout
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JInoutExcel {
    @ExcelProperty(value = "商户ID", index = 0)
    private Long deptId;
    @ExcelProperty(value = "商户名称", index = 1)
    private String deptName;
    @ExcelProperty(value = "金额", index = 2)
    private BigDecimal amount;
    @ExcelProperty(value = "出入", index = 3)
    private String direction;
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createDate;
}