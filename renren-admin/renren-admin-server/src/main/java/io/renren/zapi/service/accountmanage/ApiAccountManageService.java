package io.renren.zapi.service.accountmanage;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.accountmanage.dto.*;
import io.renren.zapi.service.allocate.dto.*;
import io.renren.zin.service.umbrella.ZinUmbrellaService;
import io.renren.zin.service.umbrella.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAccountManageService {

    @Resource
    private ApiService apiService;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;

    /**
     * 入账申请:  拿到一个applyid  +  referencecode:附言信息
     */
    public Result<VaDepositApplyResponse> depositApply(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        VaDepositApply request = apiService.<VaDepositApply>initRequest(VaDepositApply.class, context, merchantId, reqId, name, body, sign);

        TVaDepositApply tVaDepositApply = ConvertUtils.sourceToTarget(request, TVaDepositApply.class);

        // 请求通联
        TVaDepositApplyResponse response = zinUmbrellaService.depositApply(tVaDepositApply);

        // 转换回来
        VaDepositApplyResponse vaDepositApplyResponse = ConvertUtils.sourceToTarget(response, VaDepositApplyResponse.class);

        // 应答
        Result<VaDepositApplyResponse> result = new Result<>();
        result.setData(vaDepositApplyResponse);
        return result;
    }

    /**
     * 8001-入金申请确认: 需要申请id
     */
    public Result<VaDepositConfirmResponse> depositConfirm(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        VaDepositConfirm request = apiService.<VaDepositConfirm>initRequest(VaDepositConfirm.class, context, merchantId, reqId, name, body, sign);

        TVaDepositConfirm tVaDepositConfirm = ConvertUtils.sourceToTarget(request, TVaDepositConfirm.class);
        TVaDepositConfirmResponse tVaDepositConfirmResponse = zinUmbrellaService.depositConfirm(tVaDepositConfirm);
        VaDepositConfirmResponse vaDepositConfirmResponse = ConvertUtils.sourceToTarget(tVaDepositConfirmResponse, VaDepositConfirmResponse.class);

        Result<VaDepositConfirmResponse> result = new Result<>();
        result.setData(vaDepositConfirmResponse);
        return result;
    }

    /**
     * 8002-补充材料
     */
    public Result<SubmitMaterialResponse> submitMaterial(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        SubmitMaterial request = apiService.<SubmitMaterial>initRequest(SubmitMaterial.class, context, merchantId, reqId, name, body, sign);

        TSubmitMaterial tSubmitMaterial = ConvertUtils.sourceToTarget(request, TSubmitMaterial.class);
        TSubmitMaterialResponse tSubmitMaterialResponse = zinUmbrellaService.submitMaterial(tSubmitMaterial);
        SubmitMaterialResponse submitMaterialResponse = ConvertUtils.sourceToTarget(tSubmitMaterialResponse, SubmitMaterialResponse.class);

        Result<SubmitMaterialResponse> result = new Result<>();
        result.setData(submitMaterialResponse);
        return result;
    }

    /**
     * 8003-银行账户新增
     */
    public Result<MoneyAccountAddResponse> addMoneyAccount(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        MoneyAccountAdd request = apiService.<MoneyAccountAdd>initRequest(MoneyAccountAdd.class, context, merchantId, reqId, name, body, sign);

        TMoneyAccountAdd tMoneyAccountAdd = ConvertUtils.sourceToTarget(request, TMoneyAccountAdd.class);
        TMoneyAccountAddResponse tMoneyAccountAddResponse = zinUmbrellaService.addMoneyAccount(tMoneyAccountAdd);
        MoneyAccountAddResponse moneyAccountAddResponse = ConvertUtils.sourceToTarget(tMoneyAccountAddResponse, MoneyAccountAddResponse.class);

        Result<MoneyAccountAddResponse> result = new Result<>();
        result.setData(moneyAccountAddResponse);
        return result;
    }

    /**
     * 8004-银行账户修改
     */
    public Result<MoneyAccountUpdateResponse> updateMoneyAccount(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        MoneyAccountUpdate request = apiService.<MoneyAccountUpdate>initRequest(MoneyAccountUpdate.class, context, merchantId, reqId, name, body, sign);

        TMoneyAccountUpdate tMoneyAccountUpdate = ConvertUtils.sourceToTarget(request, TMoneyAccountUpdate.class);
        TMoneyAccountUpdateResponse tMoneyAccountUpdateResponse = zinUmbrellaService.updateMoneyAccount(tMoneyAccountUpdate);
        MoneyAccountUpdateResponse moneyAccountUpdateResponse = ConvertUtils.sourceToTarget(tMoneyAccountUpdateResponse, MoneyAccountUpdateResponse.class);

        Result<MoneyAccountUpdateResponse> result = new Result<>();
        result.setData(moneyAccountUpdateResponse);
        return result;
    }


    /**
     * 8005-银行账户查询
     */
    public Result<MoneyAccountQueryResponse> queryMoneyAccount(Long merchantId, String reqId, String name, String body, String sign) {
        ApiContext context = new ApiContext();
        MoneyAccountQuery request = apiService.<MoneyAccountQuery>initRequest(MoneyAccountQuery.class, context, merchantId, reqId, name, body, sign);

        TMoneyAccountQuery tMoneyAccountQuery = ConvertUtils.sourceToTarget(request, TMoneyAccountQuery.class);
        TMoneyAccountQueryResponse response = zinUmbrellaService.queryMoneyAccount(tMoneyAccountQuery);
        MoneyAccountQueryResponse moneyAccountQueryResponse = ConvertUtils.sourceToTarget(response, MoneyAccountQueryResponse.class);

        Result<MoneyAccountQueryResponse> result = new Result<>();
        result.setData(moneyAccountQueryResponse);
        return result;
    }


    /**
     * 8006-银行账户状态通知: todo
     */
    public void moneyAccountNotify(TMoneyAccountNotify notify, JMerchantEntity merchant) {
        MoneyAccountNotify moneyAccountNotify = ConvertUtils.sourceToTarget(notify, MoneyAccountNotify.class);
        apiService.notifyMerchant(moneyAccountNotify, merchant, "moneyIn");
    }

}
