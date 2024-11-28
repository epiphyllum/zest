package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_pay_channel
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JPayChannelExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "代理", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户", index = 2)
    private String merchantName;
    @ExcelProperty(value = "子商户ID", index = 3)
    private Long subId;
    @ExcelProperty(value = "子商户", index = 4)
    private String subName;
    @ExcelProperty(value = "渠道名称", index = 5)
    private String channelName;
    @ExcelProperty(value = "扣率", index = 6)
    private BigDecimal chargeRate;
    @ExcelProperty(value = "保底", index = 7)
    private BigDecimal floor;
    @ExcelProperty(value = "封顶", index = 8)
    private BigDecimal ceiling;
}