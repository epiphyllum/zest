package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_withdraw
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JWithdrawExcel {
    @ExcelProperty(value = "子商户ID", index = 0)
    private Long deptId;
    @ExcelProperty(value = "子商户", index = 1)
    private String deptName;
    @ExcelProperty(value = "商户ID", index = 2)
    private Long merchantId;
    @ExcelProperty(value = "商户", index = 3)
    private String merchantName;
    @ExcelProperty(value = "申请单流水", index = 4)
    private String meraplid;
    @ExcelProperty(value = "卡号", index = 5)
    private String cardno;
    @ExcelProperty(value = "交易对手", index = 6)
    private String payeeid;
    @ExcelProperty(value = "缴纳金额", index = 7)
    private BigDecimal amount;
    @ExcelProperty(value = "申请单号", index = 8)
    private String applyid;
    @ExcelProperty(value = "status", index = 9)
    private Integer status;
    @ExcelProperty(value = "创建时间", index = 10)
    private Date createDate;
    @ExcelProperty(value = "更新者", index = 11)
    private Long updater;
}