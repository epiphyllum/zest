package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
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
public class VCardExcel {
    @ExcelProperty(value = "发卡量", index = 0)
    private Long totalCard;
    @ExcelProperty(value = "日期", index = 1)
    private Date statDate;
    @ExcelProperty(value = "卡的币种", index = 2)
    private String currency;
    @ExcelProperty(value = "对外卡产品", index = 3)
    private String marketproduct;
    @ExcelProperty(value = "商户", index = 4)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 5)
    private String subName;
    @ExcelProperty(value = "代理", index = 6)
    private String agentName;
}