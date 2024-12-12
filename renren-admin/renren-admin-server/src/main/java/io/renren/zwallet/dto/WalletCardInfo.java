package io.renren.zwallet.dto;

import lombok.Data;

import java.util.List;

@Data
public class WalletCardInfo {
    private List<WalletCard> usdCardList;  // 美元卡列表
    private List<WalletCard> hkdCardList;  // 港币卡列表
}
