package io.renren.zwallet.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zwallet.scan.TronApi;
import lombok.Data;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

@Data
public class ChannelContext {
    JWalletDao jWalletDao;
    JWalletTxnDao jWalletTxnDao;
    JWalletConfigDao jWalletConfigDao;
    TronApi tronApi;
    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    TransactionTemplate tx;

    public ChannelContext(
            JWalletDao jWalletDao,
            JWalletConfigDao jWalletConfigDao,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            JWalletTxnDao jWalletTxnDao,
            TronApi tronApi,
            TransactionTemplate tx
    ) {
        this.jWalletDao = jWalletDao;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.jWalletConfigDao = jWalletConfigDao;
        this.jWalletTxnDao = jWalletTxnDao;
        this.tx = tx;
        this.tronApi = tronApi;
    }
}
