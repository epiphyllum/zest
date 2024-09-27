package io.renren.zin.service.cardstatus;

import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.cardstatus.dto.TCardCancelRequest;
import io.renren.zin.service.cardstatus.dto.TCardCancelResponse;
import io.renren.zin.service.cardstatus.dto.TCardChangeNotify;
import io.renren.zin.service.cardstatus.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
public class ZinCardStatusService {
    @Resource
    private ZinRequester requester;
    @Resource
    private CardStatusNotify cardStatusNotify;

    // 3200-卡激活
    public TCardActivateResponse cardActivate(TCardActivateRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/active", request, TCardActivateResponse.class);
    }

    // 3201-卡止付
    public TCardFreezeResponse cardFreeze(TCardFreezeRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/freeze", request, TCardFreezeResponse.class);
    }

    // 3202-卡解除止付
    public TCardUnfreezeResponse cardUnfreeze(TCardUnfreezeRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/unfreese", request, TCardUnfreezeResponse.class);
    }

    // 3203-卡挂失
    public TCardLossResponse cardLoss(TCardLossRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/loss", request, TCardLossResponse.class);
    }

    // 3204-卡解除挂失
    public TCardUnlossResponse cardUnloss(TCardUnlossRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/unloss", request, TCardUnlossResponse.class);
    }

    // 3205-销卡
    public TCardCancelResponse cardCancel(TCardCancelRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/cancel", request, TCardCancelResponse.class);
    }

    // 3206-解除销卡
    public TCardUncancelResponse cardUncancel(TCardUncancelRequest request) {
        return requester.request(newRequestId(), "/gcpapi/card/uncancel", request, TCardUncancelResponse.class);
    }

    // 3207-卡状态变更通知, 卡状态变更，主动发起通知合作方，涉及的状态变更的功能接口（卡挂失、解除卡挂失、卡止付、解除卡止付、销卡、解除销卡）。
    public void cardStatusChangeNotify(TCardChangeNotify notify) {
        cardStatusNotify.handle(notify);
    }

    // 3208-查询卡状态
    public TCardStatusResponse cardStatusQuery(TCardStatusQuery request) {
        return requester.request(newRequestId(), "/gcpapi/card/querycardstate", request, TCardStatusResponse.class);
    }
}
