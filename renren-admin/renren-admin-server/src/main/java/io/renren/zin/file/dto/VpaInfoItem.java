package io.renren.zin.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VpaInfoItem {
    String cardno;
    String expiredate;
    String cvv;
}
