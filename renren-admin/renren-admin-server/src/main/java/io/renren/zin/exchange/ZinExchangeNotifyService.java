package io.renren.zin.exchange;

import io.renren.zin.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 支付类申请单
// CP213  伞形账户入账
// CP201  汇款充值外部资金汇入
// CP109  离岸下发资金提现
// CP110  离岸换汇
// CP462  释放担保金
@Service
@Slf4j
public class ZinExchangeNotifyService {

    @Resource
    private CP201 cp201;
    @Resource
    private CP213 cp213;
    @Resource
    private CP109 cp109;
    @Resource
    private CP110 cp110;
    @Resource
    private CP462 cp462;

    // 申请单状态通知: 2004
    public void handle(TExchangeStateNotify notify) {
        // 汇款充值外部资金汇入: 暂时用不到
        if (notify.getTrxcode().equals("CP201")) {
            cp201.handle(notify);
            return;
        }
        // 伞形入账通知: todo!!!!
        if (notify.getTrxcode().equals("CP213")) {
            cp213.handle(notify);
            return;
        }
        // 离岸下发: 暂时没实现
        if (notify.getTrxcode().equals("CP109")) {
            cp109.handle(notify);
            return;
        }
        // 离岸换汇
        if (notify.getTrxcode().equals("CP110")) {
            cp110.handle(notify);
            return;
        }
        // 释放担保金
        if (notify.getTrxcode().equals("CP462")) {
            cp462.handle(notify);
            return;
        }
    }
}
