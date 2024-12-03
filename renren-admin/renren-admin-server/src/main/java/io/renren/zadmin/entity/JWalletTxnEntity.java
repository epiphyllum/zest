package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_wallet_txn
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_wallet_txn")
public class JWalletTxnEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;

    private Long walletId;
    private String maincardno;
    private Long channelId;
    private String channelName;

    private String currency;
    private BigDecimal stlAmount;
    private String txnCode;
    private BigDecimal txnAmount;
    private BigDecimal txnCost;
    private String usdtAddress;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}