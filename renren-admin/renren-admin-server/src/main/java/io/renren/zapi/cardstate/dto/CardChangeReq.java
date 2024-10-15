package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardChangeReq {
    private String meraplid;     //
    private String cardno;       //
    private String changetype;   //
}
