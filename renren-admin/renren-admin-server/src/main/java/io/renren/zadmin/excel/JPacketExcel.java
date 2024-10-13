package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.util.Date;

/**
 * j_packet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-13
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JPacketExcel {
    @ExcelProperty(value = "代理名", index = 0)
    private String agentName;
    @ExcelProperty(value = "商户名", index = 1)
    private String merchantName;
    @ExcelProperty(value = "请求ID", index = 2)
    private String reqId;
    @ExcelProperty(value = "接口名称", index = 3)
    private String apiName;
    @ExcelProperty(value = "接收头", index = 4)
    private String recvHeader;
    @ExcelProperty(value = "报文内容", index = 5)
    private String recv;
    @ExcelProperty(value = "报文内容", index = 6)
    private String send;
    @ExcelProperty(value = "报文内容", index = 7)
    private String sendHeader;
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createDate;
}