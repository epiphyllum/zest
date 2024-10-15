package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 卡状态查询
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardChangeQuery {
    String cardno;
}
