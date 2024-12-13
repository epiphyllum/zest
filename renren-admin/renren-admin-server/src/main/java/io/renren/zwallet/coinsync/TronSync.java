package io.renren.zwallet.coinsync;


import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.FetchResult;
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
public class TronSync {
    @Resource
    private RestTemplate restTemplate;


    @Data
    public static class FetchResult {
        //        List<JTronEntity> items;
        Long lastTimestamp;
        Integer total;
        Integer processing;
    }

    public static String url = "https://apilist.tronscanapi.com/api/filter/trc20/transfers?limit=20&start=0&sort=-timestamp&count=true&filterTokenValue=0&relatedAddress=";
    public static BigDecimal million = new BigDecimal("1000000");
    public static String USDT_CONTRACT = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    public FetchResult fetch(String accountAddress) {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url + accountAddress, String.class);
        String body = forEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(body);
        JSONArray tokenTransfers = jsonObject.getJSONArray("token_transfers");
        Integer rangeTotal = jsonObject.getInteger("rangeTotal");

//        List<Item> items = new ArrayList<>();
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

//            Item item = new Item();
//            item.setFromAddress(fromAddress);
//            item.setToAddress(toAddress);
//            item.setAmount(amount);
//            item.setTimestamp(timestamp);
//            item.setTransactionId(transactionId);
//            item.setFlag(flag);
//            items.add(item);
        }
        FetchResult fetchResult = new FetchResult();
//        fetchResult.setItems(items);
        fetchResult.setLastTimestamp(ts);
        fetchResult.setTotal(rangeTotal);
        fetchResult.setProcessing(processing);
        return fetchResult;
    }
}

