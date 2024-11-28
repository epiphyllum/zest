package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.math.BigDecimal;
	import java.util.Date;
	import io.renren.commons.mybatis.entity.BaseEntity;

/**
* j_wallet_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("j_wallet_config")
public class JWalletConfigEntity extends BaseEntity {
private static final long serialVersionUID = 1L;

			/**
			* 代理id
			*/
		private Long agentId;
			/**
			* 代理
			*/
		private String agentName;
			/**
			* 商户ID
			*/
		private Long merchantId;
			/**
			* 商户
			*/
		private String merchantName;
			/**
			* 子商户ID
			*/
		private Long subId;
			/**
			* 子商户
			*/
		private String subName;
			/**
			* 用户充值手续费率
			*/
		private BigDecimal chargeRate;
			/**
			* 最小充值港币
			*/
		private BigDecimal minHkd;
			/**
			* 最小充值美金
			*/
		private BigDecimal minUsd;
			/**
			* 最小充值u
			*/
		private BigDecimal minUsdt;
			/**
			* 更新者
			*/
			@TableField(fill = FieldFill.INSERT_UPDATE)
		private Long updater;
			/**
			* 更新时间
			*/
			@TableField(fill = FieldFill.INSERT_UPDATE)
		private Date updateDate;
}