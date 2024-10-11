package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.math.BigDecimal;
	import java.util.Date;
	import io.renren.commons.mybatis.entity.BaseEntity;

/**
* j_card_fee_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-10
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("j_card_fee_config")
public class JCardFeeConfigEntity extends BaseEntity {
private static final long serialVersionUID = 1L;

			/**
			* 产品类型
			*/
		private String producttype;
			/**
			* 卡类型
			*/
		private String cardtype;
			/**
			* 币种
			*/
		private String currency;
			/**
			* 收费
			*/
		private BigDecimal fee;
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