package io.renren.zin.service.cardapply.dto;

import lombok.Data;

// 子卡申请
@Data
public class TCardSubApplyRequest extends TCardApplyBase {
    private String maincardno; // 主卡
    private String belongtype; // 主体类型 1：员工 2：合作企业
    private String cusid;      // 子商户号 主体类型为合作企业时必填，【子商户创建】结果返回的cusid
}
