package io.renren.zmanager;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.account.dto.MoneyAccountNotify;
import io.renren.zcommon.ZapiConstant;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.TMoneyAccountAdd;
import io.renren.zin.umbrella.dto.TMoneyAccountAddResponse;
import io.renren.zin.umbrella.dto.TMoneyAccountQuery;
import io.renren.zin.umbrella.dto.TMoneyAccountQueryResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JMaccountManager {

    @Resource
    private JMaccountDao jMaccountDao;

    @Resource
    private ZinUmbrellaService zinUmbrellaService;

    @Resource
    private JMerchantDao jMerchantDao;

    @Resource
    private ApiNotifyService apiNotifyService;

    public void save(JMaccountEntity entity) {
        entity.setState(ZinConstant.MONEY_ACCOUNT_TO_VERIFY);
        entity.setApi(1);
        jMaccountDao.insert(entity);
    }

    // 提交通联
    public void submit(JMaccountEntity entity) {
        // 调用通联
        TMoneyAccountAdd tMoneyAccountAdd = ConvertUtils.sourceToTarget(entity, TMoneyAccountAdd.class);
        TMoneyAccountAddResponse tMoneyAccountAddResponse = zinUmbrellaService.addMoneyAccount(tMoneyAccountAdd);

        JMaccountEntity update = new JMaccountEntity();
        update.setId(entity.getId());
        update.setCardid(tMoneyAccountAddResponse.getId());
        jMaccountDao.updateById(update);
        entity.setCardid(tMoneyAccountAddResponse.getId());
    }

    // 查询通联创建情况
    public void query(JMaccountEntity entity) {
        TMoneyAccountQuery query = ConvertUtils.sourceToTarget(entity, TMoneyAccountQuery.class);
        query.setId(entity.getCardid());
        query.setCurrency(null);
        TMoneyAccountQueryResponse response = zinUmbrellaService.queryMoneyAccount(query);
        JMaccountEntity update = new JMaccountEntity();
        update.setId(entity.getId());
        update.setState(response.getState());
        jMaccountDao.updateById(update);

        // 接口过来
        if (entity.getApi().equals(1)) {
            log.info("接口商户, 通知来账账户创建结果");
            CompletableFuture.runAsync(() -> {
                JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
                MoneyAccountNotify moneyAccountNotify = ConvertUtils.sourceToTarget(entity, MoneyAccountNotify.class);
                moneyAccountNotify.setState(response.getState());
                apiNotifyService.notifyMerchant(moneyAccountNotify, merchant, ZapiConstant.API_moneyAccountNotify);
            });
        }
    }
}
