package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_vpa_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-01
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JVpaJobExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "代理", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户", index = 2)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 3)
    private String subName;
    @ExcelProperty(value = "创建方式", index = 4)
    private Integer api;
    @ExcelProperty(value = "场景名称", index = 5)
    private String scenename;
    @ExcelProperty(value = "场景类型", index = 6)
    private String cycle;
    @ExcelProperty(value = "最大笔数", index = 7)
    private Integer authmaxcount;
    @ExcelProperty(value = "最大金额", index = 8)
    private BigDecimal authmaxamount;
    @ExcelProperty(value = "仅限香港", index = 9)
    private String onlhkflag;
    @ExcelProperty(value = "期限开始", index = 10)
    private String begindate;
    @ExcelProperty(value = "期限结束", index = 11)
    private String endate;
    @ExcelProperty(value = "是否自然月", index = 12)
    private String naturalmonthflag;
    @ExcelProperty(value = "自然日", index = 13)
    private String naturalmonthstartday;
    @ExcelProperty(value = "是否固定金额", index = 14)
    private String fixedamountflag;
    @ExcelProperty(value = "任务状态", index = 15)
    private Integer state;
    @ExcelProperty(value = "主卡", index = 16)
    private String maincardno;
    @ExcelProperty(value = "开卡数量", index = 17)
    private Integer num;
    @ExcelProperty(value = "邮箱", index = 18)
    private String email;
    @ExcelProperty(value = "卡有效期", index = 19)
    private String cardexpiredate;
    @ExcelProperty(value = "商户发起的meraplid, 我们用id", index = 20)
    private String meraplid;
    @ExcelProperty(value = "通联返回", index = 21)
    private String applyid;
    @ExcelProperty(value = "创建时间", index = 22)
    private Date createDate;
}