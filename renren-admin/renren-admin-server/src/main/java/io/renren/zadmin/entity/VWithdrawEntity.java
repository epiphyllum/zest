package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.math.BigDecimal;
	import java.util.Date;

/**
* VIEW
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-18
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("v_withdraw")
public class VWithdrawEntity {
private static final long serialVersionUID = 1L;

			/**
			* 提现额
			*/
		private BigDecimal cardSum;
			/**
			* 平台手续费
			*/
		private BigDecimal aipCharge;
			/**
			* 手续费
			*/
		private BigDecimal charge;
			/**
			* 代理ID
			*/
		private Long agentId;
			/**
			* 代理名
			*/
		private String agentName;
			/**
			* 商户ID
			*/
		private Long merchantId;
			/**
			* 商户名
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
			* 币种
			*/
		private String currency;
			/**
			* 产品
			*/
		private String marketproduct;
			/**
			* 日期
			*/
		private Date statDate;
}