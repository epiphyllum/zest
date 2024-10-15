package io.renren.zapi.allocate;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JAllocateDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.allocate.dto.*;
import io.renren.zbalance.Ledger;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.*;
import io.renren.zmanager.JAllocateManager;
import io.renren.zmanager.JMoneyManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAllocateService {
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JAllocateDao jAllocateDao;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;
    @Resource
    private JMoneyManager jMoneyManager;
    @Resource
    private JAllocateManager jAllocateManager;

    /**
     * 入账申请:  拿到一个applyid  +  referencecode:附言信息
     */
    public Result<MoneyApplyRes> moneyApply(MoneyApply request, ApiContext context) {
        // 入库 + 调用通联
        JMoneyEntity entity = ConvertUtils.sourceToTarget(request, JMoneyEntity.class);
        entity.setApi(1);
        entity.setMerchantId(context.getMerchant().getId());
        jMoneyManager.saveAndSubmit(entity, context.getMerchant(), request.getCardId());

        // 应答
        entity = jMoneyDao.selectById(entity.getId());
        Result<MoneyApplyRes> result = new Result<>();
        MoneyApplyRes moneyApplyRes = ConvertUtils.sourceToTarget(entity, MoneyApplyRes.class);
        result.setData(moneyApplyRes);
        return result;
    }

    /**
     * 8001-入金申请确认: 需要申请id
     */
    public Result<MoneyConfirmRes> moneyConfirm(MoneyConfirm request, ApiContext apiContext) {

        // 查询申请单
        JMoneyEntity entity = jMoneyDao.selectOne(Wrappers.<JMoneyEntity>lambdaQuery()
                .eq(JMoneyEntity::getMerchantId, apiContext.getMerchant().getId())
                .eq(JMoneyEntity::getApplyid, request.getApplyid())
        );
        if (entity == null) {
            throw new RenException("无此记录");
        }

        // 确认
        jMoneyManager.confirm(entity);

        // 是个空应答: {}
        MoneyConfirmRes moneyConfirmRes = new MoneyConfirmRes();
        Result<MoneyConfirmRes> result = new Result<>();
        result.setData(moneyConfirmRes);
        return result;
    }

    /**
     * 8002-补充材料
     */
    public Result<MoneyMaterialRes> moneyMaterial(MoneyMaterial request, ApiContext apiContext) {
        // 查询申请单
        JMoneyEntity entity = jMoneyDao.selectOne(Wrappers.<JMoneyEntity>lambdaQuery()
                .eq(JMoneyEntity::getMerchantId, apiContext.getMerchant().getId())
                .eq(JMoneyEntity::getApplyid, request.getApplyid())
        );
        if (entity == null) {
            throw new RenException("无此记录");
        }

        TMaterialSubmit tMaterialSubmit = ConvertUtils.sourceToTarget(request, TMaterialSubmit.class);
        TMaterialSubmitResponse tMaterialSubmitResponse = zinUmbrellaService.submitMaterial(tMaterialSubmit);
        MoneyMaterialRes moneyMaterialRes = ConvertUtils.sourceToTarget(tMaterialSubmitResponse, MoneyMaterialRes.class);
        Result<MoneyMaterialRes> result = new Result<>();
        result.setData(moneyMaterialRes);
        return result;
    }

    // 商户转入子商户
    public Result<M2sRes> m2s(M2sReq request, ApiContext apiContext) {
        JMerchantEntity merchant = apiContext.getMerchant();
        Result<M2sRes> result = new Result<>();

        JAllocateEntity entity = ConvertUtils.sourceToTarget(request, JAllocateEntity.class);
        entity.setMerchantId(merchant.getId());
        entity.setMerchantName(merchant.getCusname());
        entity.setAgentName(merchant.getAgentName());
        entity.setAgentId(merchant.getAgentId());
        entity.setType("m2s");
        entity.setApi(1);
        jAllocateManager.handleM2s(entity);

        M2sRes m2sRes = new M2sRes();
        result.setData(m2sRes);
        return result;
    }

    // 商户转入子商户查询
    public Result<M2sQueryRes> m2sQuery(M2sQuery request, ApiContext context) {
        JAllocateEntity entity = jAllocateDao.selectOne(Wrappers.<JAllocateEntity>lambdaQuery()
                .eq(JAllocateEntity::getMeraplid, request.getMeraplid())
                .eq(JAllocateEntity::getType, "m2s")
        );
        if (entity == null) {
            throw new RenException("no record");
        }
        Result<M2sQueryRes> result = new Result<>();
        M2sQueryRes res = new M2sQueryRes();
        result.setData(res);
        return result;
    }

    // 子商户转商户
    public Result<S2mRes> s2m(S2mReq request, ApiContext context) {
        JMerchantEntity merchant = context.getMerchant();

        JAllocateEntity entity = ConvertUtils.sourceToTarget(request, JAllocateEntity.class);
        entity.setMerchantId(merchant.getId());
        entity.setMerchantName(merchant.getCusname());
        entity.setAgentName(merchant.getAgentName());
        entity.setAgentId(merchant.getAgentId());
        entity.setType("s2m");
        entity.setApi(1);
        jAllocateManager.handleS2m(entity);

        Result<S2mRes> result = new Result<>();
        result.setData(new S2mRes());
        return result;
    }

    // 子商户转商户查询
    public Result<S2mQueryRes> s2mQuery(S2mQuery request, ApiContext context) {
        JAllocateEntity entity = jAllocateDao.selectOne(Wrappers.<JAllocateEntity>lambdaQuery()
                .eq(JAllocateEntity::getMeraplid, request.getMeraplid())
                .eq(JAllocateEntity::getType, "s2m")
        );
        if (entity == null) {
            throw new RenException("no record");
        }
        Result<S2mQueryRes> result = new Result<>();
        S2mQueryRes res = new S2mQueryRes();
        result.setData(res);
        return result;
    }

}
