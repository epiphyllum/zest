package io.renren.zin.cardtxn.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


// 已入账交易明细查询 结果
@Data
@EqualsAndHashCode
public class TAuthResponse extends TResult {

    /**
     * 卡IDcardidString10Y
     * 卡号cardnoString30Y
     * 流水号logkvString50Y唯一主键
     * 交易类型trxcodeString25Y见附录【1104-授权交易类型】
     * 交易方向trxdirString6O付款：101014收款：101013
     * 交易状态stateString10O见附录【1105-授权交易状态】
     * 交易金额amountNumber18,2Y
     * 交易币种currencyString3Y
     * 预账单金额settleamountNumber18,2Y 授权的冻结的金额，
     * 预账单币种settlecurrencyString3Y 与卡账单币种一致
     * 交易时间trxtimestring20YYYYY-MM-DD HH:mm:ss
     * 商户类别代码mccString20O
     * 受理机构地址trxaddrString160O
     * 受理机构所属国家acqcountryString3Y见附录【1111-国别信息】code
     * 交易响应码respcodeString4Y
     * 交易响应描述respmsgString80O
     * 交易3DS验证标识dsflag String1O交易是否3DS验证：1-是、0-否
     * 授权码authcodeString50O
     */

    @Data
    public static class Item {
        private String cardid; // todo
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
        private String acqcountry;// todo 3
        private String respcode;// todo  4
        private String respmsg;// todo  80
        private String dsflag;  // todo  1
        private String authcode;//	授权码
    }

    private List<Item> datalist;
    private Integer total;
    private Integer pageindex;
    private Integer pagesize;


}
