package io.renren.zin.service.cardapply.dto;


import lombok.Data;

// 3006-卡申请单查询:
// 说明：用以查询主卡/子卡申请、缴纳保证金、提取保证金、卡注销申请单当前处理进度。
@Data
public class TCardApplyQuery {
    private String meraplid; // 申请单流水
    private String applyid;  // 申请单号
}
