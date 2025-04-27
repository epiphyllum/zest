package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_b2b
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2025-04-23
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JB2bExcel {
    @ExcelProperty(value = "代理名称", index = 0)
    private String agentName;
    @ExcelProperty(value = "商户名", index = 1)
    private String merchantName;
    @ExcelProperty(value = "商户b2bva", index = 2)
    private String merchantB2bva;
    @ExcelProperty(value = "String", index = 3)
    private String state;
    @ExcelProperty(value = "通知ID", index = 4)
    private String nid;
    @ExcelProperty(value = "业务关联ID", index = 5)
    private String bid;
    @ExcelProperty(value = "通联内部虚拟号", index = 6)
    private String acctno;
    @ExcelProperty(value = "交易代码", index = 7)
    private String trxcod;
    @ExcelProperty(value = "金额", index = 8)
    private BigDecimal amount;
    @ExcelProperty(value = "入账时间", index = 9)
    private String time;
    @ExcelProperty(value = "户名", index = 10)
    private String payeraccountname;
    @ExcelProperty(value = "账号", index = 11)
    private String payeraccountno;
    @ExcelProperty(value = "银行", index = 12)
    private String payeraccountbank;
    @ExcelProperty(value = "国家", index = 13)
    private String payeraccountcountry;
}