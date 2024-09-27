package io.renren.zin.service.cardtxn.dto;

import lombok.Data;

// 已入账交易明细查询
@Data
public class TAuthQuery {
    private String cardno; //	卡号
    private String state; //	交易状态		见附录【授权交易状态】
    private String trxtype; //	交易类型		见附录【授权交易类型】
    private String trxdate; //	交易日期		YYYYMMDD，交易日期&入账日期：必填其一
    private Integer pageindex; //	页码数		表示第几页，默认1
    private Integer pagesize; //	分页数		每页显示记录数，默认20，不得大于100
}
