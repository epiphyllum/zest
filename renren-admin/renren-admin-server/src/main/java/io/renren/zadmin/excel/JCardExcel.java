package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JCardExcel {

    // (6) ID
    @ExcelProperty(value = "代理ID", index = 2)
    private Long agentId;
    @ExcelProperty(value = "代理", index = 3)
    private String agentName;
    @ExcelProperty(value = "商户ID", index = 2)
    private Long merchantId;
    @ExcelProperty(value = "商户", index = 3)
    private String merchantName;
    @ExcelProperty(value = "子商户ID", index = 0)
    private Long subId;
    @ExcelProperty(value = "子商户", index = 1)
    private String subName;

    //
    @ExcelProperty(value = "产品类型", index = 4)
    private String producttype;
    @ExcelProperty(value = "卡片种类", index = 5)
    private String cardtype;
    @ExcelProperty(value = "持卡人身份", index = 6)
    private String cardholdertype;
    @ExcelProperty(value = "国籍", index = 7)
    private String nationality;
    @ExcelProperty(value = "公司职位", index = 8)
    private String companyposition;
    @ExcelProperty(value = "姓氏", index = 9)
    private String surname;
    @ExcelProperty(value = "名字", index = 10)
    private String name;
    @ExcelProperty(value = "出生日期", index = 11)
    private String birthday;
    @ExcelProperty(value = "证件1类型", index = 12)
    private String idtype;
    @ExcelProperty(value = "证件1号码", index = 13)
    private String idnumber;
    @ExcelProperty(value = "证件2类型", index = 14)
    private String idtype2;
    @ExcelProperty(value = "证件2号码", index = 15)
    private String idnumber2;
    @ExcelProperty(value = "居住国家/地区", index = 16)
    private String country;
    @ExcelProperty(value = "省份", index = 17)
    private String province;
    @ExcelProperty(value = "城市", index = 18)
    private String city;
    @ExcelProperty(value = "详细地址", index = 19)
    private String address;
    @ExcelProperty(value = "邮箱", index = 20)
    private String email;
    @ExcelProperty(value = "性别", index = 21)
    private String gender;
    @ExcelProperty(value = "手机号码所属地区", index = 22)
    private String mobilecountry;
    @ExcelProperty(value = "手机号码", index = 23)
    private String mobilenumber;
    @ExcelProperty(value = "正面照片", index = 24)
    private String photofront;
    @ExcelProperty(value = "反面照片", index = 25)
    private String photoback;
    @ExcelProperty(value = "申请费用扣款账户", index = 26)
    private String payerid;
    @ExcelProperty(value = "邮寄国家/地区", index = 27)
    private String deliverycountry;
    @ExcelProperty(value = "邮寄省份", index = 28)
    private String deliveryprovince;
    @ExcelProperty(value = "邮寄城市", index = 29)
    private String deliverycity;
    @ExcelProperty(value = "邮寄城市", index = 30)
    private String deliveryaddress;
    @ExcelProperty(value = "申请ID", index = 31)
    private String applyid;
    @ExcelProperty(value = "创建时间", index = 32)
    private Date createDate;
}