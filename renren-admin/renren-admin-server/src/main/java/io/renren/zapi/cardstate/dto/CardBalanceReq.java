package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 卡余额查询
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardBalanceReq {
    private String cardno;
}
