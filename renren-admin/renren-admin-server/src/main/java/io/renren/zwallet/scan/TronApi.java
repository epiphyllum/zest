package io.renren.zwallet.scan;

import io.renren.zadmin.entity.JWalletConfigEntity;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class TronApi {

    @Data
    public static class Account {
        private String key;
        private String address;
    }

    @Resource
    private RestTemplate restTemplate;

    public static String addressURL = "/address";
    public static String transactionInfoURL = "/transaction";
    public static String balanceTrxURL = "/trx/balance";
    public static String transferTrxURL = "/trx/transfer";
    public static String balanceUsdtURL = "/usdt/balance";
    public static String transferUsdtURL = "/usdt/transfer";

    // 创建账户
    public Account create(JWalletConfigEntity config) {
        String url = config.getTronUrl() + addressURL;
        ResponseEntity<Account> forEntity = restTemplate.getForEntity(url, Account.class);
        return forEntity.getBody();
    }

    // 账户余额
    public BigDecimal balanceTrx(JWalletConfigEntity config, String address) {
        String url = config.getTronUrl() + balanceTrxURL;
        return new BigDecimal(restTemplate.getForEntity(url, String.class).getBody());
    }

    // 账户余额
    public BigDecimal balanceUsdt(JWalletConfigEntity config, String address) {
        String url = config.getTronUrl() + balanceUsdtURL;
        return new BigDecimal(restTemplate.getForEntity(url, String.class).getBody());
    }

    // 转账trx
    public String transferTrx(JWalletConfigEntity config, String fromKey, String address) {
        String url = config.getTronUrl() + transferTrxURL;
        return restTemplate.getForEntity(url, String.class).getBody();
    }

    // 转账usdt
    public String transferUsdt(JWalletConfigEntity config, String fromKey, String address) {
        String url = config.getTronUrl() + transferUsdtURL;
        return restTemplate.getForEntity(url, String.class).getBody();
    }

    // 交易信息
    public void transactionInfo(JWalletConfigEntity config, String txid) {
        String url = config.getTronUrl() + transactionInfoURL;
        restTemplate.getForEntity(url, String.class).getBody();
    }
}
