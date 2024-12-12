package io.renren.zapi.cardmoney;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.cardmoney.dto.*;
import io.renren.zcommon.ZestConfig;
import io.renren.zmanager.JDepositManager;
import io.renren.zmanager.JWithdrawManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCardMoneyService {
    @Resource
    private JDepositManager jDepositManager;
    @Resource
    private JWithdrawManager jWithdrawManager;
    @Resource
    private JDepositDao jDepositDao;
    @Resource
    private JWithdrawDao jWithdrawDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private ZestConfig zestConfig;

    // 卡充值
    public Result<CardChargeRes> cardCharge(CardChargeReq request, ApiContext context) {

        JCardEntity card = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );
        if (card == null) {
            throw new RenException("cardno does not exists");
        }

        if (!card.getSubId().equals(request.getSubId())) {
            throw new RenException(request.getCardno() + "不属于子商户:" + request.getSubId());
        }

        JMerchantEntity merchant = context.getMerchant();

        Long subId = request.getSubId();
        JDepositEntity entity = ConvertUtils.sourceToTarget(request, JDepositEntity.class);
        entity.setMerchantId(merchant.getId());
        entity.setSubId(subId);
        entity.setCurrency(card.getCurrency());
        entity.setMarketproduct(card.getMarketproduct());
        entity.setApi(1);

        // 保存
        jDepositManager.saveAndSubmit(entity, true);

        // 应答
        entity = jDepositDao.selectById(entity.getId());
        Result<CardChargeRes> result = new Result<>();
        CardChargeRes res = new CardChargeRes(entity.getApplyid());
        result.setData(res);
        return result;
    }

    // 卡充值查询
    public Result<CardChargeQueryRes> cardChargeQuery(CardChargeQuery request, ApiContext context) {

        if (request.getMeraplid() == null && request.getApplyid() == null) {
            context.error("缺少字段applyid,meraplid: {}", request);
            throw new RenException("字段meraplyid, applyid至少填一个");
        }

        JDepositEntity entity = jDepositDao.selectOne(Wrappers.<JDepositEntity>lambdaQuery()
                .eq(request.getApplyid() != null, JDepositEntity::getApplyid, request.getApplyid())
                .eq(request.getMeraplid() != null, JDepositEntity::getMeraplid, request.getMeraplid())
        );
        if (entity == null) {
            throw new RenException("记录不存在, meraplyid = " + request.getMeraplid() + ", applyid = " + request.getApplyid());
        }

        // 调用渠道, 更新数据库
        jDepositManager.query(entity, false);

        // 应答
        entity = jDepositDao.selectById(entity.getId());
        CardChargeQueryRes res = ConvertUtils.sourceToTarget(entity, CardChargeQueryRes.class);
        res.setMerchantdeposit(entity.getMerchantDeposit());
        res.setMerchantfee(entity.getMerchantCharge());
        Result<CardChargeQueryRes> result = new Result<>();
        result.setData(res);
        return result;
    }

    // 卡提现
    public Result<CardWithdrawRes> cardWithdraw(CardWithdrawReq request, ApiContext context) {

        JCardEntity card = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );
        if (card == null) {
            throw new RenException("卡号不存在:" + request.getCardno());
        }

        JMerchantEntity merchant = context.getMerchant();
        Long subId = request.getSubId();
        JWithdrawEntity entity = ConvertUtils.sourceToTarget(request, JWithdrawEntity.class);
        entity.setMerchantId(merchant.getId());
        entity.setSubId(subId);
        entity.setCurrency(card.getCurrency());
        entity.setApi(1);

        // 保存
        jWithdrawManager.save(entity);

        // 应答: 为空对象
        Result<CardWithdrawRes> result = new Result<>();
        CardWithdrawRes res = new CardWithdrawRes(request.getMeraplid());
        result.setData(res);
        return result;
    }

    // 卡提现查询
    public Result<CardWithdrawQueryRes> cardWithdrawQuery(CardWithdrawQuery request, ApiContext context) {
        if (request.getMeraplid() == null && request.getApplyid() == null) {
            context.error("缺少字段applyid,meraplid: {}", request);
            throw new RenException("字段meraplyid, applyid至少填一个");
        }

        JWithdrawEntity entity = jWithdrawDao.selectOne(Wrappers.<JWithdrawEntity>lambdaQuery()
                .eq(request.getApplyid() != null, JWithdrawEntity::getApplyid, request.getApplyid())
                .eq(request.getMeraplid() != null, JWithdrawEntity::getMeraplid, request.getMeraplid())
        );
        if (entity == null) {
            throw new RenException("记录不存在, meraplyid = " + request.getMeraplid() + ", applyid = " + request.getApplyid());
        }

        if (entity.getApplyid() != null) {
            // 调用渠道查询, 并更新数据库
            jWithdrawManager.query(entity, false);
        }

        // 应答
        entity = jWithdrawDao.selectById(entity.getId());
        CardWithdrawQueryRes cardWithdrawQueryRes = ConvertUtils.sourceToTarget(entity, CardWithdrawQueryRes.class);
        Result<CardWithdrawQueryRes> result = new Result<>();
        result.setData(cardWithdrawQueryRes);
        return result;
    }
}
