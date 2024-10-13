package io.renren.zapi.service.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.service.exchange.dto.*;
import io.renren.zbalance.Ledger;
import io.renren.zin.service.exchange.ZinExchangeService;
import io.renren.zin.service.exchange.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


@Service
@Slf4j
public class ApiExchangeService {
    @Resource
    private  ZinExchangeService zinExchangeService;
    @Resource
    private  JExchangeDao jExchangeDao;
    @Resource
    private Ledger ledger;
    @Resource
    private TransactionTemplate tx;

    // 换汇申请
    public Result<ExchangeRes> exchange(ExchangeReq request, ApiContext context) {

        // 查找商户
        JMerchantEntity merchant = context.getMerchant();

        // 转换为实体， 添加deptId, deptName
        JExchangeEntity jExchangeEntity = ConvertUtils.sourceToTarget(request, JExchangeEntity.class);
        jExchangeEntity.setMerchantId(context.getMerchant().getId());
        jExchangeEntity.setMerchantName(merchant.getCusengname());

        // 入库申请换汇流水
        jExchangeDao.insert(jExchangeEntity);

        // 调用通联换汇申请:  商户流水号需要换成我们的流水号
        TExchangeRequest tExchangeRequest = ConvertUtils.sourceToTarget(request, TExchangeRequest.class);
        tExchangeRequest.setMeraplid(jExchangeEntity.getId().toString());
        TExchangeResponse response = zinExchangeService.exchange(tExchangeRequest);

        // 拿到申请单号, 更新
        jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                .set(JExchangeEntity::getApplyid, response.getApplyid())
        );

        // 应答商户: 就一个applyid
        ExchangeRes exchangeResponse = ConvertUtils.sourceToTarget(response, ExchangeRes.class);
        Result<ExchangeRes> result = new Result<>();
        result.setData(exchangeResponse);
        return result;
    }

    // 锁汇询价
    public Result<ExchangeLockRes> exchangeLock(ExchangeLockReq request, ApiContext context) {
        // 校验
        if (request.getApplyid() == null && request.getMeraplid() == null) {
            throw new RenException("must provide applyid or meraplid");
        }

        // 查询商户换汇申请
        JExchangeEntity jExchangeEntity = jExchangeDao.selectOne(Wrappers.<JExchangeEntity>lambdaQuery()
                .eq(request.getMeraplid() != null, JExchangeEntity::getMeraplid, request.getMeraplid())
                .eq(request.getApplyid() != null, JExchangeEntity::getApplyid, request.getApplyid())
                .eq(JExchangeEntity::getMerchantId, context.getMerchant().getId()) // 商户号
        );
        if (jExchangeEntity == null) {
            throw new RenException("请求非法");
        }

        // 转发请求到通联: 申请流水换一下
        TExchangeLockRequest tExchangeLockRequest = new TExchangeLockRequest();
        tExchangeLockRequest.setMeraplid(jExchangeEntity.getId().toString());
        TExchangeLockResponse tExchangeLockResponse = zinExchangeService.exchangeLock(tExchangeLockRequest);

        // 更新feecurrency, settlecurrency, settleamount, fxrate四个字段
        jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                .set(JExchangeEntity::getFeecurrency, tExchangeLockResponse.getFeecurrency())
                .set(JExchangeEntity::getSettlecurrency, tExchangeLockResponse.getSettlecurrency())
                .set(JExchangeEntity::getSettleamount, tExchangeLockResponse.getSettleamount())
                .set(JExchangeEntity::getFxrate, tExchangeLockResponse.getFxrate())
        );

        // 转换应答商户
        ExchangeLockRes exchangeLockResponse = ConvertUtils.sourceToTarget(tExchangeLockResponse, ExchangeLockRes.class);
        Result<ExchangeLockRes> result = new Result<>();
        result.setData(exchangeLockResponse);
        return result;
    }

    // 换汇申请单确认
    public Result<ExchangeConfirmRes> exchangeConfirm(ExchangeConfirmReq request, ApiContext context) {

        // 必须提供LK / MT
        String extype = request.getExtype();
        if("LK".equals(extype) && "MT".equals(extype)) {
            throw new RenException("extype must be either of LK or MT");
        }

        // 查询原交易
        JExchangeEntity jExchangeEntity = jExchangeDao.selectOne(Wrappers.<JExchangeEntity>lambdaQuery()
                .eq(request.getMeraplid() != null, JExchangeEntity::getMeraplid, request.getMeraplid())
                .eq(request.getApplyid() != null, JExchangeEntity::getApplyid, request.getApplyid())
                .eq(JExchangeEntity::getMerchantId, context.getMerchant().getId())
        );
        if (jExchangeEntity == null) {
            throw new RenException("请求非法");
        }

        // 锁汇价执行， 到那时前面又没有执行过锁汇
        if ("LK".equals(extype) && jExchangeEntity.getSettleamount() == null) {
            throw new RenException("没有锁汇结果， 无法按锁汇执行");
        }

        // 更新换汇的执行方式: LK/MT
        JExchangeEntity updateEntity = new JExchangeEntity();
        updateEntity.setId(jExchangeEntity.getId());
        updateEntity.setExtype(extype);
        jExchangeDao.updateById(updateEntity);

        // 转发请求到通联
        TExchangeConfirmRequest tExchangeConfirmRequest = ConvertUtils.sourceToTarget(request, TExchangeConfirmRequest.class);
        TExchangeConfirmResponse tExchangeConfirmResponse = zinExchangeService.exchangeConfirm(tExchangeConfirmRequest);

        // 将通联结果转换为商户应答: 目前都是空的
        ExchangeConfirmRes exchangeConfirmRes = ConvertUtils.sourceToTarget(tExchangeConfirmResponse, ExchangeConfirmRes.class);
        Result<ExchangeConfirmRes> result = new Result<>();
        result.setData(exchangeConfirmRes);
        return result;
    }

    // 换汇申请单查询
    public Result<ExchangeQueryRes> exchangeQuery(ExchangeQuery request, ApiContext context) {

        if (request.getApplyid() == null && request.getMeraplid() == null) {
            throw new RenException("must provide applyid or meraplid");
        }

        JExchangeEntity jExchangeEntity = jExchangeDao.selectOne(Wrappers.<JExchangeEntity>lambdaQuery()
                .eq(request.getMeraplid() != null, JExchangeEntity::getMeraplid, request.getMeraplid())
                .eq(request.getApplyid() != null, JExchangeEntity::getApplyid, request.getApplyid())
                .eq(JExchangeEntity::getMerchantId, context.getMerchant().getId())
        );
        if (jExchangeEntity == null) {
            throw new RenException("请求非法");
        }

        // 如果已经是最终状态:
        if ("06".equals(jExchangeEntity.getState()) || "07".equals(jExchangeEntity.getState())) {
            // 转换应答
            ExchangeQueryRes exchangeQueryResponse = ConvertUtils.sourceToTarget(jExchangeEntity, ExchangeQueryRes.class);
            exchangeQueryResponse.setMeraplid(jExchangeEntity.getMeraplid());  // 换回来
            Result<ExchangeQueryRes> result = new Result<>();
            result.setData(exchangeQueryResponse);
            return result;
        }


        // 转发请求到通联
        TExchangeQueryRequest tExchangeQueryRequest = ConvertUtils.sourceToTarget(request, TExchangeQueryRequest.class);
        tExchangeQueryRequest.setMeraplid(jExchangeEntity.getId().toString());
        TExchangeQueryResponse tExchangeQueryResponse = zinExchangeService.exchangeQuery(tExchangeQueryRequest);
        log.info("query response:{}", tExchangeQueryResponse);

        // 数据库状态不是成功， 查询回来时成功， 则需要做记账处理
        if (tExchangeQueryResponse.getState().equals("06") && !"06".equals(jExchangeEntity.getState())) {
            log.info("exchange succeeded, begin ledge...");
            tx.executeWithoutResult(status -> {

                jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                        .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                        .ne(jExchangeEntity.getState() != null, JExchangeEntity::getState, "06")
                        .set(JExchangeEntity::getState, "06")
                        .set(JExchangeEntity::getStlamount, tExchangeQueryResponse.getStlamount())
                        .set(JExchangeEntity::getExfxrate, tExchangeQueryResponse.getFxrate())
                        .set(JExchangeEntity::getExfee, tExchangeQueryResponse.getFee())
                );

                jExchangeEntity.setStlamount(tExchangeQueryResponse.getStlamount());
                jExchangeEntity.setExfxrate(tExchangeQueryResponse.getFxrate());
                jExchangeEntity.setExfee(tExchangeQueryResponse.getFee());
                jExchangeEntity.setState(tExchangeQueryResponse.getState());

                ledger.ledgeExchange(jExchangeEntity);
            });
        } else {
            // 只是简单更新状态
            jExchangeDao.update(null, Wrappers.<JExchangeEntity>lambdaUpdate()
                    .eq(JExchangeEntity::getId, jExchangeEntity.getId())
                    .set(JExchangeEntity::getState, tExchangeQueryResponse.getState())
                    .set(JExchangeEntity::getStlamount, tExchangeQueryResponse.getStlamount())
                    .set(JExchangeEntity::getExfxrate, tExchangeQueryResponse.getFxrate())
                    .set(JExchangeEntity::getExfxrate, tExchangeQueryResponse.getFee())
            );
        }

        // 转换应答
        ExchangeQueryRes exchangeQueryResponse = ConvertUtils.sourceToTarget(tExchangeQueryResponse, ExchangeQueryRes.class);
        exchangeQueryResponse.setMeraplid(jExchangeEntity.getMeraplid());  // 换回来
        Result<ExchangeQueryRes> result = new Result<>();
        result.setData(exchangeQueryResponse);
        return result;
    }
}
