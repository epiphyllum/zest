package io.renren.zapi.account;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.account.dto.*;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAccountService {
    @Resource
    private ZinUmbrellaService zinUmbrellaService;

    /**
     * 8003-银行账户新增
     */
    public Result<MoneyAccountAddResponse> moneyAccountAdd(MoneyAccountAdd request, ApiContext context) {
        TMoneyAccountAdd tMoneyAccountAdd = ConvertUtils.sourceToTarget(request, TMoneyAccountAdd.class);
        TMoneyAccountAddResponse tMoneyAccountAddResponse = zinUmbrellaService.addMoneyAccount(tMoneyAccountAdd);
        MoneyAccountAddResponse moneyAccountAddResponse = ConvertUtils.sourceToTarget(tMoneyAccountAddResponse, MoneyAccountAddResponse.class);

        Result<MoneyAccountAddResponse> result = new Result<>();
        result.setData(moneyAccountAddResponse);
        return result;
    }

    /**
     * 银行账户修改: todo
     */
    public Result<MoneyAccountUpdateResponse> moneyAccountUpdate(MoneyAccountUpdate request, ApiContext context) {
        TMoneyAccountUpdate tMoneyAccountUpdate = ConvertUtils.sourceToTarget(request, TMoneyAccountUpdate.class);
        TMoneyAccountUpdateResponse tMoneyAccountUpdateResponse = zinUmbrellaService.updateMoneyAccount(tMoneyAccountUpdate);
        MoneyAccountUpdateResponse moneyAccountUpdateResponse = ConvertUtils.sourceToTarget(tMoneyAccountUpdateResponse, MoneyAccountUpdateResponse.class);
        Result<MoneyAccountUpdateResponse> result = new Result<>();
        result.setData(moneyAccountUpdateResponse);
        return result;
    }

    /**
     * 银行账户查询
     */
    public Result<MoneyAccountQueryResponse> moneyAccountQuery(MoneyAccountQuery request, ApiContext context) {
        TMoneyAccountQuery tMoneyAccountQuery = ConvertUtils.sourceToTarget(request, TMoneyAccountQuery.class);
        TMoneyAccountQueryResponse response = zinUmbrellaService.queryMoneyAccount(tMoneyAccountQuery);
        MoneyAccountQueryResponse moneyAccountQueryResponse = ConvertUtils.sourceToTarget(response, MoneyAccountQueryResponse.class);
        Result<MoneyAccountQueryResponse> result = new Result<>();
        result.setData(moneyAccountQueryResponse);
        return result;
    }


    /**
     * 商户账户查询
     */
    public Result<VaAccountRes> vaAccountQuery(VaAccountQuery request, ApiContext context) {
        Result<VaAccountRes> result = new Result<>();
        return result;
    }

    /**
     * 子商户账户查询
     */
    public Result<VaSubAccountRes> vaSubAccountQuery(VaSubAccountQuery request, ApiContext context) {
        Result<VaSubAccountRes> result = new Result<>();
        return result;
    }

}
