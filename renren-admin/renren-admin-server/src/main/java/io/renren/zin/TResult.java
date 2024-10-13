package io.renren.zin;

import lombok.Data;

/**
 * 通联公共返回字段
 */
@Data
public class TResult {
    private String rspcode; //	0000表示成功，响应码参考附录【跨境平台API响应码】
    private String authcus; //	非成功响应可能不存在
    private String merid;   //	非成功响应可能不存在
    private String reqid;   //	非成功响应可能不存在
    private String rspinfo; //	响应结果描述
}