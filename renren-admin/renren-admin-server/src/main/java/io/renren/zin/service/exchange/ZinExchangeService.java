package io.renren.zin.service.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.exchange.dto.ExchangeStateNotify;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.TResult;
import io.renren.zin.service.exchange.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
public class ZinExchangeService {
    @Resource
    private ZinRequester requester;
    @Resource
    private ExchangeNotify exchangeNotify;


    // 发起换汇: 2000
    public TExchangeResponse exchange(TExchangeRequest tExchangeRequest) {
        return requester.request(newRequestId(), "/gcpapi/card/exchangeoff/apply", tExchangeRequest, TExchangeResponse.class);
    }

    // 锁汇申请: 2001
    public TExchangeLockResponse exchangeLock(TExchangeLockRequest tExchangeLockRequest) {
        return requester.request(newRequestId(), "/gcpapi/apply/lockfx", tExchangeLockRequest, TExchangeLockResponse.class);
    }

    // 换汇确认: 2002
    public TExchangeConfirmResponse exchangeConfirm(TExchangeConfirmRequest tExchangeConfirmRequest) {
        return requester.request(newRequestId(), "/gcpapi/b2bwithdraw/confirm", tExchangeConfirmRequest, TExchangeConfirmResponse.class);
    }

    // 换汇查询: 2003
    public TExchangeQueryResponse exchangeQuery(TExchangeQueryRequest tExchangeQueryRequest) {
        return requester.request(newRequestId(), "/gcpapi/apply/query", tExchangeQueryRequest, TExchangeQueryResponse.class);
    }

    // 申请单状态通知: 2004
    public void exchangeStateNotify(TExchangeStateNotify notify) {
        exchangeNotify.exchangeStateNotify(notify);
    }
}
