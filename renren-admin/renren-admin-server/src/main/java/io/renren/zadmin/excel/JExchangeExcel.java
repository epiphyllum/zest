package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_exchange
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JExchangeExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @ExcelProperty(value = "代理ID", index = 1)
    private Long agentId;
    @ExcelProperty(value = "代理名", index = 2)
    private String agentName;
    @ExcelProperty(value = "商户ID", index = 1)
    private Long deptId;
    @ExcelProperty(value = "商户名", index = 2)
    private String deptName;

    @ExcelProperty(value = "申请单流水", index = 3)
    private String meraplid;
    @ExcelProperty(value = "到账商户", index = 4)
    private String payeemerid;
    @ExcelProperty(value = "到账币种", index = 5)
    private String payeeccy;
    @ExcelProperty(value = "卖出币种", index = 6)
    private String payerccy;
    @ExcelProperty(value = "金额", index = 7)
    private BigDecimal amount;
    @ExcelProperty(value = "申请单号", index = 8)
    private String applyid;
    @ExcelProperty(value = "结算金额", index = 9)
    private BigDecimal settleamount;
    @ExcelProperty(value = "结算币种", index = 10)
    private String settlecurrency;
    @ExcelProperty(value = "汇率", index = 11)
    private BigDecimal fxrate;
    @ExcelProperty(value = "手续费", index = 12)
    private BigDecimal fee;
    @ExcelProperty(value = "锁汇方式", index = 13)
    private String extype;
    @ExcelProperty(value = "版本号", index = 14)
    private Integer version;
    @ExcelProperty(value = "创建时间", index = 15)
    private Date createDate;
}