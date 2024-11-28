package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_authed
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-11
*/
@Data
@Schema(description = "j_authed")
public class JAuthedDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private String marketproduct;

    private Long walletId;

    private String maincardno;
    private String cardno;
    private String trxtype;
    private String trxdir;
    private String state;
    private BigDecimal amount;
    private String currency;
    private BigDecimal entryamount;
    private String entrycurrency;
    private String trxtime;
    private String entrydate;
    private String chnltrxseq;
    private String trxaddr;
    private String authcode;
    private String logkv;
    private String mcc;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;
}