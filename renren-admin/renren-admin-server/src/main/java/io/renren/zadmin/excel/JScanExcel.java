package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_tron
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-12-13
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JScanExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "代理ID", index = 1)
    private Long agentId;
    @ExcelProperty(value = "代理名称", index = 2)
    private String agentName;
    @ExcelProperty(value = "商户ID", index = 3)
    private Long merchantId;
    @ExcelProperty(value = "商户名称", index = 4)
    private String merchantName;
    @ExcelProperty(value = "子商户ID", index = 5)
    private Long subId;
    @ExcelProperty(value = "子商户名称", index = 6)
    private String subName;
    @ExcelProperty(value = "钱包ID", index = 7)
    private Long walletId;
    @ExcelProperty(value = "币种", index = 8)
    private String currency;
    @ExcelProperty(value = "网络", index = 9)
    private String network;
    @ExcelProperty(value = "转出地址", index = 10)
    private String fromAddress;
    @ExcelProperty(value = "转入地址", index = 11)
    private String toAddress;
    @ExcelProperty(value = "金额", index = 12)
    private BigDecimal amount;
    @ExcelProperty(value = "时间", index = 13)
    private Long ts;
    @ExcelProperty(value = "方向", index = 14)
    private String flag;
    @ExcelProperty(value = "交易哈希", index = 15)
    private String txid;
    @ExcelProperty(value = "状态", index = 16)
    private String state;
    @ExcelProperty(value = "创建时间", index = 17)
    private Date createDate;
}