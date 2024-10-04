package io.renren.zin.config;

import lombok.Data;

import java.math.BigDecimal;


// 开卡收费标准
@Data
public class CardProductConfig {
    private String producttype;
    private String cardtype;
    private BigDecimal fee;
    private String currency;
}
