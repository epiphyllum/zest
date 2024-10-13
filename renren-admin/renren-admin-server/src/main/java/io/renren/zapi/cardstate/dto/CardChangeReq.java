package io.renren.zapi.cardstate.dto;

import lombok.Data;

@Data
public class CardChangeReq {
    private String meraplid;     //
    private String cardno;

    /**
     * 6种卡状态变更
     * cancel, cancelDrop,
     * lost, lostDrop,
     * freeze, freezeDrop
     */
    private String changetype;
}
