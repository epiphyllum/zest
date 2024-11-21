package io.renren.zapi.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.exchange.dto.*;
import io.renren.zin.exchange.ZinExchangeService;
import io.renren.zin.exchange.dto.TExchangeRateRequest;
import io.renren.zin.exchange.dto.TExchangeRateResponse;
import io.renren.zmanager.JExchangeManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ApiExchangeService {
    @Resource
    private JExchangeDao jExchangeDao;
    @Resource
    private JExchangeManager jExchangeManager;
    @Resource
    private ZinExchangeService zinExchangeService;

    // 查原交易
    private JExchangeEntity getEntity(String meraplid, String applyid, ApiContext context) {
        // 校验
        if (applyid == null && meraplid == null) {
            throw new RenException("must provide applyid or meraplid");
        }
        // 查询商户换汇申请
        JExchangeEntity entity = jExchangeDao.selectOne(Wrappers.<JExchangeEntity>lambdaQuery()
                .eq(meraplid != null, JExchangeEntity::getMeraplid, meraplid)
                .eq(applyid != null, JExchangeEntity::getApplyid, applyid)
                .eq(JExchangeEntity::getMerchantId, context.getMerchant().getId()) // 商户号
        );
        if (entity == null) {
            throw new RenException("请求非法");
        }
        return entity;
    }

    // 换汇申请
    public Result<ExchangeRes> exchange(ExchangeReq request, ApiContext context) {
        // 查找商户
        JMerchantEntity merchant = context.getMerchant();

        // 转换为实体， 添加agentId, agentName
        JExchangeEntity entity = ConvertUtils.sourceToTarget(request, JExchangeEntity.class);
        entity.setMerchantId(context.getMerchant().getId());
        entity.setMerchantName(merchant.getCusengname());
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setApi(1);

        // 入库申请换汇流水
        jExchangeManager.save(entity);

        // 提交通联: 伴随一次查询
        jExchangeManager.submit(entity);

        // 应答商户: 就一个applyid
        ExchangeRes exchangeResponse = new ExchangeRes(entity.getApplyid());
        Result<ExchangeRes> result = new Result<>();
        result.setData(exchangeResponse);
        return result;
    }

    // 锁汇询价
    public Result<ExchangeLockRes> exchangeLock(ExchangeLockReq request, ApiContext context) {
        // 查原交易
        JExchangeEntity entity = getEntity(request.getMeraplid(), request.getApplyid(), context);

        // 待确认才能锁汇
        if (!entity.getState().equals("13")) {
            throw new RenException("invalid state");
        }

        // 锁汇
        entity = jExchangeManager.lock(entity);

        // 转换应答商户
        ExchangeLockRes exchangeLockResponse = ConvertUtils.sourceToTarget(entity, ExchangeLockRes.class);
        Result<ExchangeLockRes> result = new Result<>();
        result.setData(exchangeLockResponse);
        return result;
    }

    // 换汇申请单确认
    public Result<ExchangeConfirmRes> exchangeConfirm(ExchangeConfirmReq request, ApiContext context) {

        // 必须提供LK / MT
        String extype = request.getExtype();
        if ("LK".equals(extype)) {
            throw new RenException("extype must be either of LK");  // 暂时写死
        }

        // 查原交易
        JExchangeEntity entity = getEntity(request.getMeraplid(), request.getApplyid(), context);

        // 锁汇价执行， 到那时前面又没有执行过锁汇
        if ("LK".equals(extype) && entity.getSettleamount() == null) {
            throw new RenException("没有锁汇结果， 无法按锁汇执行");
        }

        // 执行确认: 会有一次查询
        jExchangeManager.confirm(entity);
        entity = jExchangeDao.selectById(entity.getId());

        // 将通联结果转换为商户应答: 目前都是空的
        ExchangeConfirmRes exchangeConfirmRes = ConvertUtils.sourceToTarget(entity, ExchangeConfirmRes.class);
        Result<ExchangeConfirmRes> result = new Result<>();
        result.setData(exchangeConfirmRes);
        return result;
    }

    // 换汇申请单查询
    public Result<ExchangeQueryRes> exchangeQuery(ExchangeQuery request, ApiContext context) {

        // 查询原交易
        JExchangeEntity entity = getEntity(request.getMeraplid(), request.getApplyid(), context);

        // 查询通联: 已经更新了entity!!!
        jExchangeManager.query(entity, false);

        // 转换应答
        ExchangeQueryRes exchangeQueryResponse = ConvertUtils.sourceToTarget(entity, ExchangeQueryRes.class);
        exchangeQueryResponse.setMeraplid(entity.getMeraplid());  // 换回来
        Result<ExchangeQueryRes> result = new Result<>();
        result.setData(exchangeQueryResponse);
        return result;
    }

    // 汇率查询服务
    public Result<ExchangeRateQueryRes> exchangeRate(ExchangeRateQuery request, ApiContext context) {
        TExchangeRateRequest tExchangeRateRequest = ConvertUtils.sourceToTarget(request, TExchangeRateRequest.class);
        TExchangeRateResponse tExchangeRateResponse = zinExchangeService.exchangeRate(tExchangeRateRequest);
        ExchangeRateQueryRes exchangeRateQueryRes = ConvertUtils.sourceToTarget(tExchangeRateResponse, ExchangeRateQueryRes.class);
        Result<ExchangeRateQueryRes> result = new Result<>();
        result.setData(exchangeRateQueryRes);
        return result;
    }
}
