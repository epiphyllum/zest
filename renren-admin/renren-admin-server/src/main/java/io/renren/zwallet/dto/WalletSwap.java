package io.renren.zwallet.dto;

import lombok.Data;

@Data
public class WalletSwap {
    String fromCurrency;
    String toCurrency;
    String fromAmount;
    String toAmount;
}
