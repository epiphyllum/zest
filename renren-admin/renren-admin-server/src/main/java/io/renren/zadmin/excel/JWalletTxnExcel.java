package io.renren.zadmin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet_txn
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class JWalletTxnExcel {
    @ExcelProperty(value = "ID", index = 0)
    private Long id;
    @ExcelProperty(value = "代理", index = 1)
    private String agentName;
    @ExcelProperty(value = "商户", index = 2)
    private String merchantName;
    @ExcelProperty(value = "子商户", index = 3)
    private String subName;
    @ExcelProperty(value = "Integer", index = 4)
    private Integer api;
    @ExcelProperty(value = "钱包主卡", index = 5)
    private String maincardno;
    @ExcelProperty(value = "USDT渠道, OneWay渠道", index = 6)
    private String channelName;
    @ExcelProperty(value = "本币币种:HKD|USD, 如果是USD就上账到USD, 如果是HKD就上账到HKD", index = 7)
    private String currency;
    @ExcelProperty(value = "到账本币金额", index = 8)
    private BigDecimal stlAmount;
    @ExcelProperty(value = "charge:充值|withdraw:提现", index = 9)
    private String txnCode;
    @ExcelProperty(value = "交易金额${currency}", index = 10)
    private BigDecimal txnAmount;
    @ExcelProperty(value = "渠道成本", index = 11)
    private BigDecimal txnCost;
    @ExcelProperty(value = "收款地址:usdt_address, 如果是收U的话", index = 12)
    private String usdtAddress;
}