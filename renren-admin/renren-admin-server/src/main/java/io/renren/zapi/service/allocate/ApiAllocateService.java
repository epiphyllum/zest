package io.renren.zapi.service.allocate;


import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.allocate.dto.*;
import io.renren.zbalance.Ledger;
import io.renren.zin.service.umbrella.ZinUmbrellaService;
import io.renren.zin.service.umbrella.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAllocateService {

    @Resource
    private Ledger ledger;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JMerchantDao jMerchantDao;

    @Resource
    private ZinUmbrellaService zinUmbrellaService;

    /**
     * 入账申请:  拿到一个applyid  +  referencecode:附言信息
     */
    public Result<MoneyApplyRes> moneyApply(MoneyApply request, ApiContext apiContext) {
        TVaDepositApply tVaDepositApply = ConvertUtils.sourceToTarget(request, TVaDepositApply.class);
        // 请求通联
        TVaDepositApplyResponse response = zinUmbrellaService.depositApply(tVaDepositApply);
        // 转换回来
        MoneyApplyRes moneyApplyRes = ConvertUtils.sourceToTarget(response, MoneyApplyRes.class);
        // 应答
        Result<MoneyApplyRes> result = new Result<>();
        result.setData(moneyApplyRes);
        return result;
    }

    /**
     * 8001-入金申请确认: 需要申请id
     */
    public Result<MoneyConfirmRes> moneyConfirm(MoneyConfirm request, ApiContext apiContext) {
        TVaDepositConfirm tVaDepositConfirm = ConvertUtils.sourceToTarget(request, TVaDepositConfirm.class);
        TVaDepositConfirmResponse tVaDepositConfirmResponse = zinUmbrellaService.depositConfirm(tVaDepositConfirm);
        MoneyConfirmRes moneyConfirmRes = ConvertUtils.sourceToTarget(tVaDepositConfirmResponse, MoneyConfirmRes.class);
        Result<MoneyConfirmRes> result = new Result<>();
        result.setData(moneyConfirmRes);
        return result;
    }

    /**
     * 8002-补充材料
     */
    public Result<MoneyMaterialRes> moneyMaterial(MoneyMaterial request, ApiContext apiContext) {
        TSubmitMaterial tSubmitMaterial = ConvertUtils.sourceToTarget(request, TSubmitMaterial.class);
        TSubmitMaterialResponse tSubmitMaterialResponse = zinUmbrellaService.submitMaterial(tSubmitMaterial);
        MoneyMaterialRes moneyMaterialRes = ConvertUtils.sourceToTarget(tSubmitMaterialResponse, MoneyMaterialRes.class);
        Result<MoneyMaterialRes> result = new Result<>();
        result.setData(moneyMaterialRes);
        return result;
    }

    // 商户转入子商户
    public Result<M2sRes> m2s(M2sReq request, ApiContext apiContext) {
        // 记账
        Result<M2sRes> result = new Result<>();
        return result;
    }

    // 商户转入子商户查询
    public Result<M2sQueryRes> m2sQuery(Long merchantId, String reqId, String name, String body, String sign) {
        Result<M2sQueryRes> result = new Result<>();
        return result;
    }

    // 子商户转商户
    public Result<S2mRes> s2m(S2mReq request, ApiContext context) {
        JMerchantEntity merchant = context.getMerchant();
        JAllocateEntity entity = new JAllocateEntity();
        entity.setMerchantName(merchant.getCusname());
        ledger.ledgeS2m(entity);
        Result<S2mRes> result = new Result<>();
        return result;
    }

    // 子商户转商户查询
    public Result<S2mQueryRes> s2mQuery(S2mQuery request, ApiContext context) {
        Result<S2mQueryRes> result = new Result<>();
        return result;
    }

}
