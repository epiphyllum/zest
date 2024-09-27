package io.renren.zin.service.cardapply.dto;

import lombok.Data;

// 3008 - 变更VPA子卡场景信息
@Data
public class TCardVpaApply {
    private String sceneid; // 场景IDsceneidString30Y
    private String maincardno; // 主卡卡号maincardnoString30Y主卡产品类型为：021201:通华VPA电子卡的主卡卡号
    private String num; // 申请数量numString6O为空，默认申请1张，一次申请最大数量为10万
    private String email; // 接受卡片邮箱emailString50O为空，不发送VPA子卡信息，需自行到用卡平台下载
}
