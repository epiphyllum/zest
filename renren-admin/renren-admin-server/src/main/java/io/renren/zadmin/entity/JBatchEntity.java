package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.util.Date;
	import io.renren.commons.mybatis.entity.BaseEntity;

/**
* j_batch
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-19
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("j_batch")
public class JBatchEntity extends BaseEntity {
private static final long serialVersionUID = 1L;

			/**
			* 任务名称
			*/
		private String batchType;
			/**
			* 任务状态
			*/
		private String state;
			/**
			* 任务日期
			*/
		private String batchDate;
			/**
			* 备注
			*/
		private String memo;
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