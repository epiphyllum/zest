package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_vpa_log
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-01
*/
@Data
@Schema(description = "j_vpa_log")
public class JVpaJobDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "代理id")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "创建方式")
    private Integer api;
    private String sceneid;
    @Schema(description = "场景名称")
    private String scenename;

    @Schema(description = "场景类型")
    private String cycle;

    @Schema(description = "交易币种")
    private String currency;

    @Schema(description = "最大笔数")
    private Integer authmaxcount;
    @Schema(description = "最大金额")
    private BigDecimal authmaxamount;
    @Schema(description = "仅限香港")
    private String onlhkflag;
    @Schema(description = "期限开始")
    private String begindate;
    @Schema(description = "期限结束")
    private String enddate;
    @Schema(description = "是否自然月")
    private String naturalmonthflag;
    @Schema(description = "自然日")
    private String naturalmonthstartday;
    @Schema(description = "是否固定金额")
    private String fixedamountflag;
    @Schema(description = "任务状态")
    private String state;

    @Schema(description = "开卡费用")
    private BigDecimal merchantfee;
    @Schema(description = "费用币种")
    private String feecurrency;

    @Schema(description = "主卡")
    private String maincardno;
    @Schema(description = "卡产品")
    private String marketproduct;
    @Schema(description = "本币币种")
    private String productcurrency;
    @Schema(description = "开卡数量")
    private Integer num;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "卡有效期")
    private String cardexpiredate;
    @Schema(description = "商户发起的meraplid, 我们用id")
    private String meraplid;
    @Schema(description = "通联返回")
    private String applyid;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}