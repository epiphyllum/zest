package io.renren.zapi.account;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.account.dto.*;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.TMoneyAccountAdd;
import io.renren.zin.umbrella.dto.TMoneyAccountAddResponse;
import io.renren.zin.umbrella.dto.TMoneyAccountQuery;
import io.renren.zin.umbrella.dto.TMoneyAccountQueryResponse;
import io.renren.zmanager.JMaccountManager;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiAccountService {
    @Resource
    private JMaccountManager jMaccountManager;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private JBalanceDao jBalanceDao;

    // 提交通联
    private TMoneyAccountAddResponse submit(JMaccountEntity entity){
        // 调用通联
        TMoneyAccountAdd tMoneyAccountAdd = ConvertUtils.sourceToTarget(entity, TMoneyAccountAdd.class);
        TMoneyAccountAddResponse tMoneyAccountAddResponse = zinUmbrellaService.addMoneyAccount(tMoneyAccountAdd);

        JMaccountEntity update = new JMaccountEntity();
        update.setId(entity.getId());
        update.setCardid(tMoneyAccountAddResponse.getId());
        jMaccountDao.updateById(update);

        return tMoneyAccountAddResponse;
    }

    /**
     * 8003-银行账户新增
     */
    public Result<MoneyAccountAddResponse> moneyAccountAdd(MoneyAccountAdd request, ApiContext context) {
        JMerchantEntity merchant = context.getMerchant();

        // 准备入库entity
        JMaccountEntity entity = ConvertUtils.sourceToTarget(request, JMaccountEntity.class);
        entity.setMerchantId(merchant.getId());
        entity.setMerchantName(merchant.getCusname());
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setApi(1);
        try {
            jMaccountManager.save(entity);
        } catch (DuplicateKeyException ex) {
            throw new RenException("卡号重复");
        }

        // 提交通联
        jMaccountManager.submit(entity);

        // 转换结果返回
        MoneyAccountAddResponse response =  new MoneyAccountAddResponse(entity.getCardid());
        Result<MoneyAccountAddResponse> result = new Result<>();
        result.setData(response);
        return result;
    }

    /**
     * 银行账户修改: todo
     */
    public Result<MoneyAccountUpdateResponse> moneyAccountUpdate(MoneyAccountUpdate request, ApiContext context) {
        return null;
    }

    /**
     * 银行账户查询
     *
     * T-待提交 0-待审核；1-审核通过；2-审核不通过；4-冻结；5-关闭；6-待复审
     */
    public Result<MoneyAccountQueryResponse> moneyAccountQuery(MoneyAccountQuery request, ApiContext context) {
        Result<MoneyAccountQueryResponse> result = new Result<>();

        JMerchantEntity merchant = context.getMerchant();
        JMaccountEntity jMaccountEntity = jMaccountDao.selectOne(Wrappers.<JMaccountEntity>lambdaQuery()
                .eq(JMaccountEntity::getMeraplid, request.getMeraplid())
                .eq(JMaccountEntity::getMerchantId, merchant.getId()));
        if (jMaccountEntity == null) {
            result.setCode(404);
            result.setMsg("记录不存在");
            return result;
        }

        // 已经是终态
        String state = jMaccountEntity.getState();
        if ("1".equals(state) || "1".equals(state) || "5".equals(state)) {
            MoneyAccountQueryResponse response = ConvertUtils.sourceToTarget(jMaccountEntity, MoneyAccountQueryResponse.class);
            result.setData(response);
            return result;
        }

        // 不是终态, 还没提交通联
        if ("T".equals(state)) {
            submit(jMaccountEntity);
        }

        // 查询通联
        TMoneyAccountQuery tMoneyAccountQuery = ConvertUtils.sourceToTarget(jMaccountEntity, TMoneyAccountQuery.class);
        TMoneyAccountQueryResponse response = zinUmbrellaService.queryMoneyAccount(tMoneyAccountQuery);

        // 更新数据库
        JMaccountEntity update = new JMaccountEntity();
        update.setId(jMaccountEntity.getId());
        update.setState(response.getState());
        jMaccountDao.updateById(update);

        // 应答
        MoneyAccountQueryResponse apiResponse = ConvertUtils.sourceToTarget(response, MoneyAccountQueryResponse.class);
        result.setData(apiResponse);
        return result;
    }


    /**
     * 商户账户查询
     */
    public Result<VaAccountRes> vaAccountQuery(VaAccountQuery request, ApiContext context) {
        Result<VaAccountRes> result = new Result<>();
        List<JBalanceEntity> balanceEntities = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                .likeRight(JBalanceEntity::getBalanceType, "VA_")
                .eq(request.getCurrency() != null, JBalanceEntity::getCurrency, request.getCurrency())
                .eq(JBalanceEntity::getOwnerId, context.getMerchant().getId())
        );
        List<AccountItem> accountItems = ConvertUtils.sourceToTarget(balanceEntities, AccountItem.class);

        VaAccountRes res = new VaAccountRes(accountItems);
        res.setItems(accountItems);
        result.setData(res);
        return result;

    }

    /**
     * 子商户账户查询
     */
    public Result<VaSubAccountRes> vaSubAccountQuery(VaSubAccountQuery request, ApiContext context) {
        Result<VaSubAccountRes> result = new Result<>();
        // todo: 确保他的parent是 merchantid

        List<JBalanceEntity> balanceEntities = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                .likeRight(JBalanceEntity::getBalanceType, "SUB_VA_")
                .eq(request.getCurrency() != null, JBalanceEntity::getCurrency, request.getCurrency())
                .eq(JBalanceEntity::getOwnerId, request.getSubId())
        );
        List<AccountItem> accountItems = ConvertUtils.sourceToTarget(balanceEntities, AccountItem.class);

        VaSubAccountRes res = new VaSubAccountRes(accountItems);
        res.setItems(accountItems);
        result.setData(res);
        return result;
    }

}
