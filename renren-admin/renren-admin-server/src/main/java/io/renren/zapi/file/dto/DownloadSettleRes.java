package io.renren.zapi.file.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DownloadSettleRes {
    @Data
    public static class Item {
        private Long merchantId;
        private String merchantName;
        private Long subId;
        private String subName;
        private String marketproduct;
        //
        private String maincardno;
        // info
        private String cardno;
        private String trxcode;
        private String trxdir;
        private String state;
        private BigDecimal amount;
        private String currency;
        private BigDecimal entryamount;
        private String entrycurrency;
        private String trxtime;
        private String entrydate;
        private String chnltrxseq;
        private String trxaddr;
        private String authcode;
        private String logkv;
        private String mcc;
    }
    private List<Item> items;
}
