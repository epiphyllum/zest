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
@EqualsAndHashCode(callSuper=false)
@TableName("j_wallet_txn")
public class JWalletTxnEntity extends BaseEntity {
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
		private Integer api;
			/**
			* 钱包ID
			*/
		private Long walletId;
			/**
			* 钱包主卡
			*/
		private String maincardno;
			/**
			* 收款渠道
			*/
		private Long channelId;
			/**
			* USDT渠道, OneWay渠道
			*/
		private String channelName;
			/**
			* 本币币种:HKD|USD, 如果是USD就上账到USD, 如果是HKD就上账到HKD
			*/
		private String currency;
			/**
			* 到账本币金额
			*/
		private BigDecimal stlAmount;
			/**
			* charge:充值|withdraw:提现
			*/
		private String txnCode;
			/**
			* 交易金额${currency}
			*/
		private BigDecimal txnAmount;
			/**
			* 渠道成本
			*/
		private BigDecimal txnCost;
			/**
			* 收款地址:usdt_address, 如果是收U的话
			*/
		private String usdtAddress;
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