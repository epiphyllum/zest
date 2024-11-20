package io.renren.zmanager;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.TMoneyAccountAdd;
import io.renren.zin.umbrella.dto.TMoneyAccountAddResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JMaccountManager {

    @Resource
    private JMaccountDao jMaccountDao;

    @Resource
    private ZinUmbrellaService zinUmbrellaService;

    public void save(JMaccountEntity entity) {
        entity.setState(ZinConstant.MONEY_ACCOUNT_TO_VERIFY);
        entity.setApi(1);
        jMaccountDao.insert(entity);
    }

    // 提交通联
    public void submit(JMaccountEntity entity){
        // 调用通联
        TMoneyAccountAdd tMoneyAccountAdd = ConvertUtils.sourceToTarget(entity, TMoneyAccountAdd.class);
        TMoneyAccountAddResponse tMoneyAccountAddResponse = zinUmbrellaService.addMoneyAccount(tMoneyAccountAdd);

        JMaccountEntity update = new JMaccountEntity();
        update.setId(entity.getId());
        update.setCardid(tMoneyAccountAddResponse.getId());
        jMaccountDao.updateById(update);
        entity.setCardid(tMoneyAccountAddResponse.getId());
    }
}
