package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_mcard
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JMcardExcel {
    @ExcelProperty(value = "商户ID", index = 0)
    private Long deptId;
    @ExcelProperty(value = "商户名", index = 1)
    private String deptName;
    @ExcelProperty(value = "String", index = 2)
    private String meraplid;
    @ExcelProperty(value = "String", index = 3)
    private String producttype;
    @ExcelProperty(value = "String", index = 4)
    private String cardtype;
    @ExcelProperty(value = "String", index = 5)
    private String cardholdertype;
    @ExcelProperty(value = "String", index = 6)
    private String nationality;
    @ExcelProperty(value = "String", index = 7)
    private String companyposition;
    @ExcelProperty(value = "String", index = 8)
    private String surname;
    @ExcelProperty(value = "String", index = 9)
    private String birthday;
    @ExcelProperty(value = "String", index = 10)
    private String idtype;
    @ExcelProperty(value = "String", index = 11)
    private String idnumber;
    @ExcelProperty(value = "String", index = 12)
    private String idtype2;
    @ExcelProperty(value = "String", index = 13)
    private String idnumber2;
    @ExcelProperty(value = "String", index = 14)
    private String province;
    @ExcelProperty(value = "String", index = 15)
    private String city;
    @ExcelProperty(value = "String", index = 16)
    private String address;
    @ExcelProperty(value = "String", index = 17)
    private String email;
    @ExcelProperty(value = "String", index = 18)
    private String gender;
    @ExcelProperty(value = "String", index = 19)
    private String mobilecountry;
    @ExcelProperty(value = "String", index = 20)
    private String mobilenumber;
    @ExcelProperty(value = "String", index = 21)
    private String photofront;
    @ExcelProperty(value = "String", index = 22)
    private String photoback;
    @ExcelProperty(value = "String", index = 23)
    private String payerid;
    @ExcelProperty(value = "String", index = 24)
    private String deliverycountry;
    @ExcelProperty(value = "String", index = 25)
    private String deliveryprovince;
    @ExcelProperty(value = "String", index = 26)
    private String deliverycity;
    @ExcelProperty(value = "String", index = 27)
    private String deliveryaddress;
    @ExcelProperty(value = "申请ID", index = 28)
    private String applyid;
    @ExcelProperty(value = "余额", index = 29)
    private BigDecimal balance;
    @ExcelProperty(value = "创建时间", index = 30)
    private Date createDate;
}