package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardChangeQueryRes {
    private String state;
    private String cardno;
}
