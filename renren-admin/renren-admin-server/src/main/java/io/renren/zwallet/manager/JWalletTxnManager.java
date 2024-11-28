package io.renren.zwallet.manager;

import io.renren.zwallet.channel.ChannelFactory;
import io.renren.zwallet.dto.WalletChargeRequest;
import io.renren.zwallet.dto.WalletChargeResponse;
import io.renren.zwallet.dto.WalletWithdrawRequest;
import io.renren.zwallet.dto.WalletWithdrawResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JWalletTxnManager {
    @Resource
    private ChannelFactory channelFactory;

    // 钱包充值
    public WalletChargeResponse charge(WalletChargeRequest request) {
        return null;
    }

    // 钱包提现: 咱不支持
    public WalletWithdrawResponse withdraw(WalletWithdrawRequest request) {
        return null;
    }
}
