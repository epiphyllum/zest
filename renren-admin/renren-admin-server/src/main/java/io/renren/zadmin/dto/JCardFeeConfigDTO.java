package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_card_fee_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-10
*/
@Data
@Schema(description = "j_card_fee_config")
public class JCardFeeConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "产品类型")
    private String producttype;
    @Schema(description = "卡类型")
    private String cardtype;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "收费")
    private BigDecimal fee;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}