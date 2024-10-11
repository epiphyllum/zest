package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_card_fee_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-10
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JCardFeeConfigExcel {
    @ExcelProperty(value = "产品类型", index = 0)
    private String producttype;
    @ExcelProperty(value = "卡类型", index = 1)
    private String cardtype;
    @ExcelProperty(value = "币种", index = 2)
    private String currency;
    @ExcelProperty(value = "收费", index = 3)
    private BigDecimal fee;
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createDate;
}