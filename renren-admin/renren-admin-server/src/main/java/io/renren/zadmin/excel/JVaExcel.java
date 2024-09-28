package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_va
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JVaExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    // (4) 业务字段
    @ExcelProperty(value = "通联虚拟户", index = 2)
    private String accountno;
    @ExcelProperty(value = "币种", index = 3)
    private String currency;
    @ExcelProperty(value = "余额", index = 4)
    private BigDecimal amount;
    @ExcelProperty(value = "收款账户号", index = 5)
    private String vaaccountno;

    // (2)
    @ExcelProperty(value = "创建时间", index = 6)
    private Date createDate;
    @ExcelProperty(value = "更新时间", index = 7)
    private Date updateDate;
}