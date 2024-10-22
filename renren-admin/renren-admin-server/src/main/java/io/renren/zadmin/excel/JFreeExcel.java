package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_free
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-21
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JFreeExcel {
    @ExcelProperty(value = "Long", index = 0)
    private Long id;
    @ExcelProperty(value = "金额", index = 1)
    private BigDecimal amount;
    @ExcelProperty(value = "币种", index = 2)
    private String currency;
    @ExcelProperty(value = "唯一ID", index = 3)
    private String applyid;
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createDate;
}