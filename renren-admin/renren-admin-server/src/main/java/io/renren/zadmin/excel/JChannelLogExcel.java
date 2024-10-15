package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.util.Date;

/**
 * j_channel_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-15
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JChannelLogExcel {
    @ExcelProperty(value = "请求ID", index = 0)
    private String reqId;
    @ExcelProperty(value = "接口名称", index = 1)
    private String apiName;
    @ExcelProperty(value = "收到", index = 2)
    private String recv;
    @ExcelProperty(value = "发送", index = 3)
    private String send;
    @ExcelProperty(value = "签名", index = 4)
    private String sign;
}