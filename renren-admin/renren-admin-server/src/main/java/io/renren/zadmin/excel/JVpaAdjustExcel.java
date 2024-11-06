package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_vpa_adjust
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-02
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JVpaAdjustExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "代理", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户", index = 2)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 3)
    private String subName;
    @ExcelProperty(value = "vpa子卡", index = 4)
    private String cardno;
    @ExcelProperty(value = "调整金额", index = 5)
    private BigDecimal adjustAmount;
    @ExcelProperty(value = "调整期额度", index = 6)
    private BigDecimal oldQuote;
    @ExcelProperty(value = "调整后额度", index = 7)
    private BigDecimal newQuote;
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createDate;
}