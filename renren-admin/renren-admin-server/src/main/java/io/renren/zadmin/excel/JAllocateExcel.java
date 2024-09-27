package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class JAllocateExcel {

    @ExcelProperty(value = "代理ID", index = 0)
    private Long agentId;
    @ExcelProperty(value = "代理", index = 0)
    private String agentName;
    @ExcelProperty(value = "商户ID", index = 0)
    private Long merchantId;
    @ExcelProperty(value = "商户", index = 0)
    private String merchantName;
    @ExcelProperty(value = "子商户ID", index = 0)
    private Long subId;
    @ExcelProperty(value = "子商户", index = 0)
    private String subName;

    // 调拨类型:  i2v,  v2i, s2m, m2s
    @ExcelProperty(value = "调拨类型", index = 0)
    private String type;
    @ExcelProperty(value = "金额", index = 0)
    private BigDecimal amount;
    @ExcelProperty(value = "币种", index = 0)
    private String currency;

    // 商户ID/子商户ID,
    @ExcelProperty(value = "出金ID", index = 0)
    private Long fromId;
    @ExcelProperty(value = "入金ID", index = 0)
    private Long toId;
    @ExcelProperty(value = "出金账户", index = 0)
    private Long fromName;
    @ExcelProperty(value = "入金账户", index = 0)
    private Long toName;

    @ExcelProperty(value = "操作员", index = 0)
    private Long updater;
    @ExcelProperty(value = "更新时间", index = 0)
    private Date updateDate;

}