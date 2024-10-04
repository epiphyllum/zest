package io.renren.zin.service.exchange;

import io.renren.commons.tools.exception.RenException;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// CP201  汇款充值外部资金汇入
// CP213  伞形账户入账
// CP109  离岸下发资金提现
// CP110  离岸换汇
// CP462  释放担保金

@Service
@Slf4j
public class ExchangeNotify {


    @Resource
    private CP109 cp109;
    @Resource
    private CP110 cp110;
    @Resource
    private CP201 cp201;
    @Resource
    private CP213 cp213;
    @Resource
    private CP462 cp462;


    // 换汇通知
    public void exchangeStateNotify(TExchangeStateNotify notify) {
        String state = notify.getState();

        int status = 0;

        // 支付类申请单中间态
        if (state.equals("01") ||
                state.equals("05") ||
                state.equals("16") ||
                state.equals("12") ||
                state.equals("23") ||
                state.equals("38")
        ) {
            status = 0;
        }

        // 支付类申请单: 终态-成功
        else if (state.equals("06")) {
            status = 1;
        }

        // 支付类申请单: 终态-失败
        else if (state.equals("02") ||
                state.equals("07") ||
                state.equals("11")
        ) {
            status = 2;
        } else {
            throw new RenException("状态不在通联列表内");
        }

        // 汇款充值外部资金汇入
        if (notify.getTrxcode().equals("CP201")) {
            cp201.handle(notify, status);
            return;
        }
        // 伞形入账通知
        if (notify.getTrxcode().equals("CP213")) {
            log.info("伞形入账通知处理...");
            cp213.handle(notify, status);
            return;
        }
        // 离岸下发资金提现
        if (notify.getTrxcode().equals("CP109")) {
            cp109.handle(notify, status);
            return;
        }
        // 离岸换汇
        if (notify.getTrxcode().equals("CP110")) {
            cp110.handle(notify, status);
            return;
        }
        // 释放担保金
        if (notify.getTrxcode().equals("CP462")) {
            cp462.handle(notify, status);
            return;
        }
    }
}
