package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JDepositExcel {
    @ExcelProperty(value = "子商户ID", index = 0)
    private Long deptId;
    @ExcelProperty(value = "子商户", index = 1)
    private String deptName;
    @ExcelProperty(value = "商户ID", index = 2)
    private Long merchantId;
    @ExcelProperty(value = "商户", index = 3)
    private String merchantName;
    @ExcelProperty(value = "申请单", index = 4)
    private String meraplid;
    @ExcelProperty(value = "卡号", index = 5)
    private String cardno;
    @ExcelProperty(value = "付款id", index = 6)
    private String payerid;
    @ExcelProperty(value = "金额", index = 7)
    private BigDecimal amount;
    @ExcelProperty(value = "交易对上", index = 8)
    private String payeeaccount;
    @ExcelProperty(value = "采购内容", index = 9)
    private String procurecontent;
    @ExcelProperty(value = "合同文件", index = 10)
    private String agmfid;
    @ExcelProperty(value = "申请单号", index = 11)
    private String applyid;
    @ExcelProperty(value = "状态", index = 12)
    private Integer status;
    @ExcelProperty(value = "创建时间", index = 13)
    private Date createDate;
}