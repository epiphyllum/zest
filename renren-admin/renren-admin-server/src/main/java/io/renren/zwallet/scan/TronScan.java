package io.renren.zwallet.scan;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.renren.zadmin.entity.JScanEntity;
import io.renren.zadmin.entity.JWalletEntity;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TronScan {
    @Resource
    private RestTemplate restTemplate;

    @Data
    public static class FetchResult {
        List<JScanEntity> items;
        Long lastTimestamp;
        Integer total;
        Integer processing;
    }

    public static String url = "https://apilist.tronscanapi.com/api/filter/trc20/transfers?limit=20&start=0&sort=-timestamp&count=true&filterTokenValue=0&relatedAddress=";
    public static BigDecimal million = new BigDecimal("1000000");
    public static String USDT_CONTRACT = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    public FetchResult fetch(String accountAddress, JWalletEntity walletEntity) {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url + accountAddress, String.class);
        String body = forEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(body);
        JSONArray tokenTransfers = jsonObject.getJSONArray("token_transfers");
        Integer rangeTotal = jsonObject.getInteger("rangeTotal");

        List<JScanEntity> items = new ArrayList<>();
        Long ts = -1L;
        Integer processing = 0;
        for (Object tokenTransfer : tokenTransfers) {
            JSONObject object = (JSONObject) tokenTransfer;
            String contractAddress = object.getString("contract_address");
            if (!contractAddress.equals(USDT_CONTRACT)) {
                continue;
            }
            String finalResult = object.getString("finalResult");
            if (!finalResult.equals("SUCCESS")) {
                processing++;
                continue;
            }
            Long timestamp = object.getLong("block_ts");
            if (timestamp > ts) {
                ts = timestamp;
            }

            String transactionId = object.getString("transaction_id");
            String fromAddress = object.getString("from_address");
            String toAddress = object.getString("to_address");
            String flag = fromAddress.equals(accountAddress) ? "-" : "+";

            BigDecimal amount = new BigDecimal(object.getString("quant")).divide(million).setScale(2, RoundingMode.HALF_UP);

            System.out.println(fromAddress + "|" + toAddress + "|" + timestamp + "|" + finalResult + "|" + flag + amount);

            JScanEntity item = new JScanEntity();
            item.setFromAddress(fromAddress);
            item.setToAddress(toAddress);
            item.setAmount(amount);
            item.setTs(timestamp);
            item.setTxid(transactionId);
            item.setFlag(flag);
            item.setCurrency("USDT");
            item.setNetwork("trc20");
            fillItem(item, walletEntity);
            items.add(item);
        }
        FetchResult fetchResult = new FetchResult();
        fetchResult.setItems(items);
        fetchResult.setLastTimestamp(ts);
        fetchResult.setTotal(rangeTotal);
        fetchResult.setProcessing(processing);
        return fetchResult;
    }

    private void fillItem(JScanEntity item, JWalletEntity walletEntity) {
        item.setAgentId(walletEntity.getAgentId());
        item.setMerchantId(walletEntity.getAgentId());
        item.setSubId(walletEntity.getAgentId());
        item.setAgentName(walletEntity.getAgentName());
        item.setMerchantName(walletEntity.getMerchantName());
        item.setSubName(walletEntity.getSubName());
        item.setWalletId(walletEntity.getId());
    }
}

