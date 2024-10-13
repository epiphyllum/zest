package io.renren.zin.cardtxn.dto;

import io.renren.zin.TResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


// 已入账交易明细查询 结果
@Data
public class TAuthResponse extends TResult {

    @Data
    public static class Item {
        private String cardno;//	卡号
        private String logkv;//	流水号			 非联机交易中，可能为空值
        private String trxcode;//	交易类型			见附录【授权交易类型】
        private String trxdir;//	交易方向			付款：101014收款：101013
        private String state;//	交易状态			见附录【授权交易状态】
        private BigDecimal amount;//	交易金额
        private String currency;//	交易币种
        private BigDecimal settleamount;//	入账金额
        private String settlecurrency;//	入账币种
        private String trxtime;//	交易时间			YYYY-MM-DD HH:mm:ss
        private String mcc;//	商户类别代码
        private String trxaddr;//	交易地点
        private String authcode;//	授权码
    }

    private List<Item> datalist;
    private Integer total;
    private Integer pageindex;
    private Integer pagesize;


}
