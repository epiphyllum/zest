package io.renren.zapi.service.sub.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class SubCreateRes extends TResult {
    private String cusid;
}