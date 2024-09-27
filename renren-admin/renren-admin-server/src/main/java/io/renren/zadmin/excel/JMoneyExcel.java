package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.util.Date;

/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JMoneyExcel {

    @ExcelProperty(value = "商户号", index = 11)
    private Long merchantId;
    @ExcelProperty(value = "商户名", index = 12)
    private String merchantName;

    @ExcelProperty(value = "通知id", index = 0)
    private String nid;
    @ExcelProperty(value = "业务关联id", index = 1)
    private String bid;
    @ExcelProperty(value = "账号", index = 2)
    private String acctno;
    @ExcelProperty(value = "变动金额", index = 3)
    private String amount;
    @ExcelProperty(value = "交易类型", index = 4)
    private String trxcod;
    @ExcelProperty(value = "入账时间", index = 5)
    private String time;
    @ExcelProperty(value = "打款方姓名", index = 6)
    private String payeraccountname;
    @ExcelProperty(value = "打款方银行账号", index = 7)
    private String payeraccountno;
    @ExcelProperty(value = "打款方银行号", index = 8)
    private String payeraccountbank;
    @ExcelProperty(value = "打款方国家", index = 9)
    private String payeraccountcountry;
    @ExcelProperty(value = "附言", index = 10)
    private String ps;

    @ExcelProperty(value = "创建时间", index = 13)
    private Date createDate;
}