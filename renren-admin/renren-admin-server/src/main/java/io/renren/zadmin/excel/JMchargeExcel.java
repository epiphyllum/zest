package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_merchant_charge
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JMchargeExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;


    @ExcelProperty(value = "代理ID", index = 0)
    private Long agentId;
    @ExcelProperty(value = "代理名", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户ID", index = 4)
    private Long merchantId;
    @ExcelProperty(value = "商户", index = 5)
    private String merchantName;
    @ExcelProperty(value = "子商户ID", index = 2)
    private Long subId;
    @ExcelProperty(value = "子商户", index = 3)
    private String subName;


    @ExcelProperty(value = "打款方姓名", index = 3)
    private String payeraccountname;
    @ExcelProperty(value = "打款方银行账号", index = 4)
    private String payeraccountno;
    @ExcelProperty(value = "打款方银行号", index = 5)
    private String payeraccountbank;
    @ExcelProperty(value = "打款方国家", index = 6)
    private String payeraccountcountry;
    @ExcelProperty(value = "通知id", index = 7)
    private String nid;
    @ExcelProperty(value = "业务关联id", index = 8)
    private String bid;
    @ExcelProperty(value = "账号", index = 9)
    private String acctno;
    @ExcelProperty(value = "变动金额", index = 10)
    private BigDecimal amount;
    @ExcelProperty(value = "交易类型", index = 11)
    private String trxcod;
    @ExcelProperty(value = "入账时间", index = 12)
    private String time;
    @ExcelProperty(value = "创建时间", index = 13)
    private Date createDate;
}