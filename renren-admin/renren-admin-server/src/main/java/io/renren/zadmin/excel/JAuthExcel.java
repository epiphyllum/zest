package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JAuthExcel {

    // (6) ID
    @ExcelProperty(value = "代理ID", index = 0)
    private Long agentId;
    @ExcelProperty(value = "代理", index = 1)
    private Long agentName;
    @ExcelProperty(value = "商户ID", index = 2)
    private Long merchantId;
    @ExcelProperty(value = "商户", index = 3)
    private Long merchantName;
    @ExcelProperty(value = "子商户ID", index = 4)
    private Long subId;
    @ExcelProperty(value = "子商户", index = 5)
    private Long subName;

    // todo
    @ExcelProperty(value = "卡号", index = 6)
    private String cardno;
    @ExcelProperty(value = "流水号", index = 7)
    private String logkv;
    @ExcelProperty(value = "交易类型", index = 8)
    private String trxtype;
    @ExcelProperty(value = "交易方向", index = 9)
    private String trxdir;
    @ExcelProperty(value = "交易状态", index = 10)
    private String state;
    @ExcelProperty(value = "交易金额", index = 11)
    private BigDecimal amount;
    @ExcelProperty(value = "币种", index = 12)
    private String currency;
    @ExcelProperty(value = "交易时间", index = 13)
    private String trxtime;
    @ExcelProperty(value = "商户类别代码", index = 14)
    private String mcc;
    @ExcelProperty(value = "交易地点", index = 15)
    private String trxaddr;
    @ExcelProperty(value = "授权码", index = 16)
    private String authcode;
    @ExcelProperty(value = "创建时间", index = 17)
    private Date createDate;
}