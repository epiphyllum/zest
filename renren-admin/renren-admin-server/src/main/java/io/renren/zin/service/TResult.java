package io.renren.zin.service;

import lombok.Data;

/**
 * 通联公共返回字段
 */
@Data
public class TResult {
    private String rspcode;  //	String	8	Y	0000表示成功，响应码参考附录【跨境平台API响应码】
    private String authcus; //	String	15	C	非成功响应可能不存在
    private String merid;   //	String	15	O	非成功响应可能不存在
    private String reqid;   //	String	32	C	非成功响应可能不存在
    private String rspinfo; //	String	300	Y	响应结果描述
}