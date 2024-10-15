package io.renren.zapi.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 更新账户信息
@Data
public class MoneyAccountUpdate extends MoneyAccountAdd {
    String id;   // 账户ID
}
