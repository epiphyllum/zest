package io.renren.zwallet.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zbalance.ledgers.Ledger610WalletCharge;
import lombok.Data;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

@Data
public class ChannelContext {
    JWalletDao jWalletDao;
    JWalletTxnDao jWalletTxnDao;
    JWalletConfigDao jWalletConfigDao;
    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    Ledger610WalletCharge ledger610WalletCharge;
    TransactionTemplate tx;

    public ChannelContext(
            JWalletDao jWalletDao,
            JWalletConfigDao jWalletConfigDao,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            JWalletTxnDao jWalletTxnDao,
            Ledger610WalletCharge ledger610WalletCharge,
    TransactionTemplate tx
    ) {
        this.jWalletDao = jWalletDao;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.jWalletConfigDao = jWalletConfigDao;
        this.jWalletTxnDao = jWalletTxnDao;
        this.ledger610WalletCharge = ledger610WalletCharge;
        this.tx = tx;
    }
}
