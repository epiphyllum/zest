package io.renren.zapi.sub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubQueryRes{
    private Long subId;     // 通联子商户号
    private String cusname; // 客户名
    private String state;   // 状态  "NA": 审核,  "04": 审核通过  "05": 审核失败
}
