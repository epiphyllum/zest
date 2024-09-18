package io.renren.zin.service.sub.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TSubQueryResponse extends TResult {
    private String ctid;    //	String	15	Y	相同注册的唯一标识
    private String cusid;   //	String	15	Y	 
    private String cusname; // 客户名	cusname	String	100	Y	 
    private String state;   //状态	state	String	2	Y	00：审核中; 01：已注册; 其他情况为空;
}
