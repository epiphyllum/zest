package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class VpaJobNotify {
    private List<JobItem> items;
}