package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.math.BigDecimal;
	import java.util.Date;
	import io.renren.commons.mybatis.entity.BaseEntity;

/**
* j_tron
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-12-13
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("j_scan")
public class JScanEntity extends BaseEntity {
private static final long serialVersionUID = 1L;

			/**
			* 代理ID
			*/
		private Long agentId;
			/**
			* 代理名称
			*/
		private String agentName;
			/**
			* 商户ID
			*/
		private Long merchantId;
			/**
			* 商户名称
			*/
		private String merchantName;
			/**
			* 子商户ID
			*/
		private Long subId;
			/**
			* 子商户名称
			*/
		private String subName;
			/**
			* 钱包ID
			*/
		private Long walletId;
			/**
			* 币种
			*/
		private String currency;
			/**
			* 网络
			*/
		private String network;
			/**
			* 转出地址
			*/
		private String fromAddress;
			/**
			* 转入地址
			*/
		private String toAddress;
			/**
			* 金额
			*/
		private BigDecimal amount;
			/**
			* 时间
			*/
		private Long ts;
			/**
			* 方向
			*/
		private String flag;
			/**
			* 交易哈希
			*/
		private String txid;
			/**
			* 状态
			*/
		private String state;
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