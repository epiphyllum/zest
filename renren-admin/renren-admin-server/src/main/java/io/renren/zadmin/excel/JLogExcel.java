package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JLogExcel {
    // (3)
    @ExcelProperty(value = "归属方ID", index = 0)
    private Long ownerId;
    @ExcelProperty(value = "归属方", index = 1)
    private String ownerName;
    @ExcelProperty(value = "归属方", index = 2)
    private String ownerType;

    @ExcelProperty(value = "余额类型", index = 3)
    private String balanceType;
    @ExcelProperty(value = "余额名称", index = 4)
    private String balanceName;
    @ExcelProperty(value = "余额ID", index = 5)
    private Long balanceId;
    @ExcelProperty(value = "币种", index = 6)
    private String currency;
    @ExcelProperty(value = "凭证类型", index = 7)
    private String factType;
    @ExcelProperty(value = "凭证ID", index = 8)
    private Long factId;
    @ExcelProperty(value = "凭证金额", index = 9)
    private BigDecimal factAmount;
    @ExcelProperty(value = "凭证描述", index = 10)
    private String factMemo;
    @ExcelProperty(value = "旧余额", index = 11)
    private BigDecimal oldBalance;
    @ExcelProperty(value = "新余额", index = 12)
    private BigDecimal newBalance;


    @ExcelProperty(value = "创建时间", index = 13)
    private Date createDate;
}