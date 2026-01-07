package io.renren.zin.cardtxn;

import io.renren.zin.ZinRequester;
import io.renren.zin.cardtxn.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static io.renren.zcommon.CommonUtils.uniqueId;

@Service
public class ZinCardTxnService {

    @Resource
    private ZinRequester requester;
    @Resource
    private ZinCardTxnNotifyService zinCardTxnNotifyService;

    // 4000-授权交易明细查询
    public TAuthResponse authQuery(TAuthQuery query) {
        return requester.request(uniqueId(), "/gcpapi/card/qryauthtrans", query, TAuthResponse.class);
    }

    // 4001-已入账交易明细查询
    public TAuthSettledResponse settledQuery(TAuthSettledQuery query) {
        return requester.request(uniqueId(), "/gcpapi/card/qrytrans", query, TAuthSettledResponse.class);
    }

    // 同步结算流水
    public void syncSettledAuth(String date) {
        TAuthSettledQuery request = new TAuthSettledQuery();
        request.setEntrydate(date);
        request.setPagesize(100);
        int pageIndex = 1;
        request.setPageindex(pageIndex);

//        while(true) {
//            TAuthSettledResponse response = this.settledQuery(request);
//        }

    }
}
