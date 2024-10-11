package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_authed
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-11
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JAuthedExcel {
    @ExcelProperty(value = "Long", index = 0)
    private Long id;
    @ExcelProperty(value = "String", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户名称", index = 2)
    private String merchantName;
    @ExcelProperty(value = "String", index = 3)
    private String subName;
    @ExcelProperty(value = "String", index = 4)
    private String cardno;
    @ExcelProperty(value = "String", index = 5)
    private String trxtype;
    @ExcelProperty(value = "String", index = 6)
    private String trxdir;
    @ExcelProperty(value = "String", index = 7)
    private String state;
    @ExcelProperty(value = "BigDecimal", index = 8)
    private BigDecimal amount;
    @ExcelProperty(value = "String", index = 9)
    private String currency;
    @ExcelProperty(value = "BigDecimal", index = 10)
    private BigDecimal entryamount;
    @ExcelProperty(value = "String", index = 11)
    private String entrycurrency;
    @ExcelProperty(value = "String", index = 12)
    private String trxtime;
    @ExcelProperty(value = "String", index = 13)
    private String entrydate;
    @ExcelProperty(value = "String", index = 14)
    private String chnltrxseq;
    @ExcelProperty(value = "String", index = 15)
    private String trxaddr;
    @ExcelProperty(value = "String", index = 16)
    private String authcode;
    @ExcelProperty(value = "String", index = 17)
    private String logkv;
    @ExcelProperty(value = "String", index = 18)
    private String mcc;
    @ExcelProperty(value = "创建时间", index = 19)
    private Date createDate;
}