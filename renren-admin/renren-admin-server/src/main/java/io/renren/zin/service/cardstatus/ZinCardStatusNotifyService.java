package io.renren.zin.service.cardstatus;

import io.renren.zin.service.cardstatus.dto.TCardChangeNotify;
import org.springframework.stereotype.Service;

@Service
public class ZinCardStatusNotifyService {

    // 3207-卡状态变更通知, 卡状态变更，主动发起通知合作方，涉及的状态变更的功能接口（卡挂失、解除卡挂失、卡止付、解除卡止付、销卡、解除销卡）。
    public void cardStatusChangeNotify(TCardChangeNotify notify) {
    }
}
