package io.renren.zapi.cardstate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.cardstate.dto.*;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardmoney.ZinCardMoneyService;
import io.renren.zin.cardmoney.dto.TCardBalanceRequest;
import io.renren.zin.cardmoney.dto.TCardBalanceResponse;
import io.renren.zin.cardstate.ZinCardStateService;
import io.renren.zin.cardstate.dto.*;
import io.renren.zmanager.JCardManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ApiCardStateService {

    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStateService zinCardStateService;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private ZestConfig zestConfig;

    @Resource
    private JCardManager jCardManager;

    private JCardEntity getCardEntity(String cardid, String cardno, ApiContext context) {
        // 查询卡
        JCardEntity entity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(cardno != null, JCardEntity::getCardno, cardno)
                .eq(cardid != null, JCardEntity::getCardid, cardid)
        );
        if (entity == null) {
            throw new RenException("no record");
        }
        // 看是否为这个商户的卡
        if (!entity.getMerchantId().equals(context.getMerchant().getId())) {
            throw new RenException("no record");
        }
        return entity;
    }

    // 卡状态变更申请
    public Result<CardChangeRes> cardChange(CardChangeReq request, ApiContext context) {
        if (request.getChangetype() == null) {
            throw new RenException("missing field changetype");
        }
        if (request.getMeraplid() == null) {
            throw new RenException("missing field meraplid");
        }
        if (request.getCardno() == null) {
            throw new RenException("missing field cardno");
        }

        switch (request.getChangetype()) {
            case CardStateChangeType.CARD_LOSS:
                TCardLossRequest tCardLossRequest = ConvertUtils.sourceToTarget(request, TCardLossRequest.class);
                zinCardStateService.cardLoss(tCardLossRequest);
                break;
            case CardStateChangeType.CARD_UNLOSS:
                TCardUnlossRequest tCardUnlossRequest = ConvertUtils.sourceToTarget(request, TCardUnlossRequest.class);
                zinCardStateService.cardUnloss(tCardUnlossRequest);
                break;
            case CardStateChangeType.CARD_CANCEL:
                TCardCancelRequest tCardCancelRequest = ConvertUtils.sourceToTarget(request, TCardCancelRequest.class);
                zinCardStateService.cardCancel(tCardCancelRequest);
                break;
            case CardStateChangeType.CARD_UNCANCEL:
                TCardUncancelRequest tCardUncancelRequest = ConvertUtils.sourceToTarget(request, TCardUncancelRequest.class);
                zinCardStateService.cardUncancel(tCardUncancelRequest);
                break;
            case CardStateChangeType.CARD_FREEZE:
                TCardFreezeRequest tCardFreezeRequest = ConvertUtils.sourceToTarget(request, TCardFreezeRequest.class);
                zinCardStateService.cardFreeze(tCardFreezeRequest);
                break;
            case CardStateChangeType.CARD_UNFREEZE:
                TCardUnfreezeRequest tCardUnfreezeRequest = ConvertUtils.sourceToTarget(request, TCardUnfreezeRequest.class);
                zinCardStateService.cardUnfreeze(tCardUnfreezeRequest);
                break;
            default:
                throw new RenException("unsupported change type");
        }

        JCardEntity cardEntity = getCardEntity(request.getCardid(), request.getCardno(), context);
        jCardManager.queryCard(cardEntity);

        Result<CardChangeRes> result = new Result<>();
        result.setData(new CardChangeRes());
        return result;
    }

    // 卡状态查询
    public Result<CardChangeQueryRes> cardChangeQuery(CardChangeQuery request, ApiContext context) {

        // 查询卡信息
        JCardEntity entity = getCardEntity(request.getCardid(), request.getCardno(), context);

        if ( ZinConstant.isCardApplyFail(entity.getState()) || ZinConstant.isCardApplySuccess(entity.getState())) {
            // 终态情况下, 不需要查询通联
        } else {
            // 查询卡状态
            jCardManager.queryCard(entity);
        }

        // 应答
        Result<CardChangeQueryRes> result = new Result<>();
        CardChangeQueryRes res = new CardChangeQueryRes(entity.getCardstate(), entity.getCardno());
        result.setData(res);
        return result;
    }

    // 卡余额查询
    public Result<CardBalanceRes> cardBalance(CardBalanceReq request, ApiContext context) {
        // 查询卡信息: 确保卡是这个商户的
        JCardEntity entity = getCardEntity(request.getCardid(), request.getCardno(), context);

        // 调用通联
        TCardBalanceRequest tCardBalanceRequest = ConvertUtils.sourceToTarget(request, TCardBalanceRequest.class);
        TCardBalanceResponse balance = zinCardMoneyService.balance(tCardBalanceRequest);
        CardBalanceRes cardBalanceRes = ConvertUtils.sourceToTarget(balance, CardBalanceRes.class);

        // 应答
        Result<CardBalanceRes> result = new Result<>();
        result.setData(cardBalanceRes);
        return result;
    }

    // 卡支付信息: cvv2 | expiredate
    public Result<CardPayInfoRes> cardPayInfo(CardPayInfoReq request, ApiContext context) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(request.getCardno() != null, JCardEntity::getCardno, request.getCardno())
                .eq(request.getCardid() != null, JCardEntity::getCardid, request.getCardid())
        );

        JMerchantEntity merchant = context.getMerchant();
        String plainCvv = CommonUtils.decryptSensitiveString(cardEntity.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "utf-8");
        String merchantCvv = CommonUtils.encryptSensitiveString(plainCvv, merchant.getSensitiveKey(), "utf-8");
        String merchantExpiredate = CommonUtils.encryptSensitiveString(cardEntity.getExpiredate(), merchant.getSensitiveKey(), "utf-8");

        CardPayInfoRes res = new CardPayInfoRes();
        res.setCardno(request.getCardno());
        res.setCvv(merchantCvv);
        res.setExpiredate(merchantExpiredate);

        Result<CardPayInfoRes> result = new Result<>();
        result.setData(res);
        return result;
    }

    public Result<CardInfoRes> cardInfo(CardInfoReq request, ApiContext context) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(request.getCardno() != null, JCardEntity::getCardno, request.getCardno())
                .eq(request.getCardid() != null, JCardEntity::getCardid, request.getCardid())
        );
        jCardManager.balanceCard(cardEntity);
        CardInfoRes cardInfoRes = ConvertUtils.sourceToTarget(cardEntity, CardInfoRes.class);
        Result<CardInfoRes> result = new Result<>();
        result.setData(cardInfoRes);
        return result;
    }

}
