package io.renren.zapi.cardstate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 查询卡支付信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoRes {
    private String cardno;           // 卡号
    private String marketproduct;    // 卡产品
    private String cardtype;         // 卡片种类
    private String belongtype;       // 持卡人身份
    private String maincardno;       // 主卡卡号
    private String nationality;      // 国际
    private String companyposition;  // 职位
    private String surname;          // 姓
    private String name;             // 名
    private String birthday;         // 生日
    private String idtype;           // 证件1类型
    private String idnumber;         // 证件1号码
    private String idtype2;          // 证件2类型
    private String idnumber2;        // 证件2号码

    private String state;            // 卡申请状态
    private String stateexplain;     // 卡申请状态描述
    private String cardstate;        // 卡状态

    private BigDecimal balance;      // 余额
}
