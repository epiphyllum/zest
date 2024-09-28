package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.util.Date;

/**
 * j_maccount
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JMaccountExcel {

    // id
    @ExcelProperty(value = "代理ID", index = 0)
    private Long agentId;
    @ExcelProperty(value = "代理名", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户ID", index = 0)
    private Long merchantId;
    @ExcelProperty(value = "商户名", index = 1)
    private String merchantName;

    // 业务
    @ExcelProperty(value = "打款方姓名", index = 2)
    private String payeraccountname;
    @ExcelProperty(value = "打款方银行账号", index = 3)
    private String payeraccountno;
    @ExcelProperty(value = "打款方银行号", index = 4)
    private String payeraccountbank;
    @ExcelProperty(value = "打款方国家", index = 5)
    private String payeraccountcountry;

    // 通用字段
    @ExcelProperty(value = "创建时间", index = 6)
    private Date createDate;
}