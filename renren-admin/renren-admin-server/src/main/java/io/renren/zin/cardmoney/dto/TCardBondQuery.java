package io.renren.zin.cardmoney.dto;

import lombok.Data;

@Data
public class TCardBondQuery {
    private String cardno;
    private String trxcode;
    private String createtimebegin;
    private String createtimeend;
}
