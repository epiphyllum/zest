package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
	import java.util.Date;
	import io.renren.commons.mybatis.entity.BaseEntity;

/**
* j_packet
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-13
*/
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("j_packet")
public class JPacketEntity extends BaseEntity {
private static final long serialVersionUID = 1L;

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
			* 请求ID
			*/
		private String reqId;
			/**
			* 接口名称
			*/
		private String apiName;
			/**
			* 接收头
			*/
		private String recvHeader;
			/**
			* 报文内容
			*/
		private String recv;
			/**
			* 报文内容
			*/
		private String send;
			/**
			* 报文内容
			*/
		private String sendHeader;
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