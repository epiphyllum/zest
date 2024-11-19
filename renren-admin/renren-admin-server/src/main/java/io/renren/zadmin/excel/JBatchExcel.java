package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.util.Date;

/**
 * j_batch
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JBatchExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "任务名称", index = 1)
    private String batchType;
    @ExcelProperty(value = "任务状态", index = 2)
    private String state;
    @ExcelProperty(value = "任务日期", index = 3)
    private Date batchDate;
    @ExcelProperty(value = "备注", index = 4)
    private String memo;
    @ExcelProperty(value = "创建时间", index = 5)
    private Date createDate;
    @ExcelProperty(value = "更新时间", index = 6)
    private Date updateDate;
}