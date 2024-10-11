package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.math.BigDecimal;
	import java.util.Date;
	import io.renren.commons.mybatis.entity.BaseEntity;

/**
* j_authed
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-11
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("j_authed")
public class JAuthedEntity extends BaseEntity {
private static final long serialVersionUID = 1L;

		private Long agentId;
		private String agentName;
			/**
			* 商户ID
			*/
		private Long merchantId;
			/**
			* 商户名称
			*/
		private String merchantName;
		private Long subId;
		private String subName;
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
			/**
			* 乐观锁版本号
			*/
		private Integer version;
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