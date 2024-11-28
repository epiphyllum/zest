package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.util.Date;

/**
 * j_wallet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-27
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JWalletExcel {
    @ExcelProperty(value = "代理", index = 0)
    private String agentName;
    @ExcelProperty(value = "商户", index = 1)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 2)
    private String subName;
    @ExcelProperty(value = "钱包名称", index = 3)
    private String walletName;
    @ExcelProperty(value = "String", index = 4)
    private String maincardno;
    @ExcelProperty(value = "创建时间", index = 5)
    private Date createDate;
}