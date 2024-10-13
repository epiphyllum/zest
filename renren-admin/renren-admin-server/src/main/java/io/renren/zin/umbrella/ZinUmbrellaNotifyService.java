package io.renren.zin.umbrella;


import io.renren.zin.umbrella.dto.TMoneyAccountNotify;
import org.springframework.stereotype.Service;

@Service
public class ZinUmbrellaNotifyService {

    /**
     * 8006-银行账户状态通知
     * 接口：合作方自定义/bnfauditrst  方向：通联->合作方
     * 说明：银行账户添加申请被审核后，会触发该通知。如果审核不通过，可以从通知的状态描述
     */
    public void moneyAccountStatusNotify(TMoneyAccountNotify notify) {
    }

}
