package io.renren.zapi.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaSubAccountQuery {
    String currency;  // 币种
    Long subId;       // 子商户ID
}
