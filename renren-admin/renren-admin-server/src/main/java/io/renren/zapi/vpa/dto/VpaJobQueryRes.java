package io.renren.zapi.vpa.dto;

import lombok.Data;

@Data
public class VpaJobQueryRes {
    private String state;
    private String encrypted;  // 加密信息
}
