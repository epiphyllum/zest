package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@Schema(description = "j_card")
public class JCardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

    //
    @Schema(description = "完成日期")
    private Date statDate;

    @Schema(description = "对外卡产品")
    private String marketproduct;
    @Schema(description = "主卡")
    private String maincardno;
    @Schema(description = "主卡id")
    private Long maincardid;

    @Schema(description = "钱包ID")
    private Long walletId;
    @Schema(description = "钱包名")
    private String walletName;

    // ID相关
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    private Integer api;

    // 业务字段(7)
    @Schema(description = "申请单流水")
    private String meraplid;
    @Schema(description = "卡产品")
    private String producttype;
    @Schema(description = "卡片种类")
    private String cardtype;
    @Schema(description = "持卡人身份")
    private String belongtype;
    @Schema(description = "通联子商户号")
    private String cusid;
    @Schema(description = "持卡人身份")
    private String cardholdertype;

    // 2
    @Schema(description = "国籍")
    private String nationality;
    @Schema(description = "公司职位")
    private String companyposition;
    // 3
    @Schema(description = "姓氏")
    private String surname;
    @Schema(description = "名字")
    private String name;
    @Schema(description = "出生日期")
    private String birthday;

    // 4
    @Schema(description = "证件1类型")
    private String idtype;
    @Schema(description = "证件1号码")
    private String idnumber;
    @Schema(description = "证件2类型")
    private String idtype2;
    @Schema(description = "证件2号码")
    private String idnumber2;

    //  4
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "居住国家/地区")
    private String country;
    @Schema(description = "省份")
    private String province;
    @Schema(description = "城市")
    private String city;
    @Schema(description = "详细地址")
    private String address;


    @Schema(description = "手机号码所属地区")
    private String mobilecountry;
    @Schema(description = "手机号码")
    private String mobilenumber;
    @Schema(description = "邮政编码")
    private String deliverypostcode;

    @Schema(description = "正面照片")
    private String photofront;
    @Schema(description = "反面照片")
    private String photoback;
    @Schema(description = "正面照片2")
    private String photofront2;
    @Schema(description = "反面照片2")
    private String photoback2;

    @Schema(description = "申请费用扣款账户")
    private String payerid;

    @Schema(description = "邮寄国家/地区")
    private String deliverycountry;
    @Schema(description = "邮寄省份")
    private String deliveryprovince;
    @Schema(description = "邮寄城市")
    private String deliverycity;
    @Schema(description = "邮寄城市")
    private String deliveryaddress;

    @Schema(description = "交易对手")
    private String payeeaccount;
    @Schema(description = "采购内容")
    private String procurecontent;
    @Schema(description = "保证金合同")
    private String agmfid;

    // 大吉设计
    @Schema(description = "币种ID")
    private String currency;
    @Schema(description = "商户费用")
    private BigDecimal merchantfee;
    @Schema(description = "通知次数")
    private Integer notifyCount;
    @Schema(description = "通知状态")
    private Integer notifyStatus;

    // 通联返回
    @Schema(description = "申请ID")
    private String applyid;
    @Schema(description = "平台ID")
    private String txnid;

    // 结果
    @Schema(description = "fee")
    private BigDecimal fee;           // 申请费用
    @Schema(description = "feecurrency")
    private String feecurrency;       // 申请费用币种
    @Schema(description = "cardno")
    private String cardno;            // 卡号

    @Schema(description = "state")
    private String state;             // 卡申请状态
    @Schema(description = "stateexplain")
    private String stateexplain;      // 卡申请状态
    @Schema(description = "cardstate")
    private String cardstate;         // 卡申请状态

    // 卡余额
    @Schema(description = "balance")
    private BigDecimal balance;
    @Schema(description = "prepaidQuota")
    private BigDecimal prepaidQuota;


    @Schema(description = "cvv")
    private String cvv;
    @Schema(description = "expiredate")
    private String expiredate;

    // 共享卡相关信息
    @Schema(description = "场景ID")
    private String sceneid;
    @Schema(description = "场景名称")
    private String scenename;

    @Schema(description = "使用场景")
    private String cycle;
    @Schema(description = "最大消费笔数")
    private Integer authmaxcount;
    @Schema(description = "最大消费金额")
    private BigDecimal authmaxamount;
    @Schema(description = "仅限香港")
    private String onlhkflag;
    @Schema(description = "起始日期")
    private String begindate;
    @Schema(description = "结束日期")
    private String enddate;
    @Schema(description = "自然月标志")
    private String naturalmonthflag;
    @Schema(description = "非自然月是起始日期")
    private String naturalmonthstartday;
    @Schema(description = "单次固定金额")
    private String fixedamountflag;
    @Schema(description = "允许币种")
    private String permitCurrency;
    @Schema(description = "那个vpa任务创建的卡")
    private Long vpaJob;
}