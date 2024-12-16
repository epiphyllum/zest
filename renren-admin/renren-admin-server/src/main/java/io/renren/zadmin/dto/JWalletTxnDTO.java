package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet_txn
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@Schema(description = "j_wallet_txn")
public class JWalletTxnDTO implements Serializable {
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
    private Integer api;

    @Schema(description = "钱包ID")
    private Long walletId;
    @Schema(description = "钱包ID")
    private String walletName;

    @Schema(description = "钱包ID")
    private String fromCurrency;
    @Schema(description = "钱包ID")
    private String toCurrency;
    @Schema(description = "钱包ID")
    private BigDecimal fromAmount;
    @Schema(description = "钱包ID")
    private BigDecimal toAmount;
    @Schema(description = "交易代码")
    private String txnCode;
    @Schema(description = "txnMemo")
    private String txnMemo;

    @Schema(description = "状态")
    private String state;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}