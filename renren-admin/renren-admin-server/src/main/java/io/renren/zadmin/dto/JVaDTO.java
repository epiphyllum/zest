package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_va
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Data
@Schema(description = "j_va")
public class JVaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 5
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

    // 业务字段(4)
    @Schema(description = "通联ID")
    private String tid;
    @Schema(description = "通联虚拟户")
    private String accountno;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "余额")
    private BigDecimal amount;
    @Schema(description = "收款账户号")
    private String vaaccountno;

}