package io.renren.zin.sub.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TSubCreateResponse extends TResult {
    private String cusid;
}