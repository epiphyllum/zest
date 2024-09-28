package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_merchant
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JMerchantExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    // (2) ID
    @ExcelProperty(value = "代理ID", index = 1)
    private Long agentId;
    @ExcelProperty(value = "代理", index = 2)
    private String agentName;

    // 业务字段
    @ExcelProperty(value = "跟踪号", index = 4)
    private String meraplid;
    @ExcelProperty(value = "商户名称", index = 5)
    private String cusname;
    @ExcelProperty(value = "商户性质", index = 6)
    private String flag;
    @ExcelProperty(value = "营业执照名称", index = 7)
    private String buslicensename;
    @ExcelProperty(value = "注册地", index = 8)
    private String areacode;
    @ExcelProperty(value = "客户英文名称", index = 9)
    private String cusengname;
    @ExcelProperty(value = "联系电话", index = 10)
    private String tel;
    @ExcelProperty(value = "子商户号", index = 11)
    private String cusid;
    @ExcelProperty(value = "状态", index = 12)
    private String state;
    @ExcelProperty(value = "审核", index = 13)
    private Integer verify;
    @ExcelProperty(value = "充值费率", index = 14)
    private BigDecimal chargeRate;
    @ExcelProperty(value = "失败费", index = 15)
    private BigDecimal failFee;
    @ExcelProperty(value = "商户类型", index = 16)
    private String mcc;
    @ExcelProperty(value = "启用", index = 17)
    private Integer enabled;

    // 通用
    @ExcelProperty(value = "创建时间", index = 18)
    private Date createDate;
}