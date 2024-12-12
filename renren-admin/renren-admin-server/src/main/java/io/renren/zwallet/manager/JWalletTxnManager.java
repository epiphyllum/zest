package io.renren.zwallet.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JCardManager;
import io.renren.zwallet.channel.ChannelFactory;
import io.renren.zwallet.channel.PayChannel;
import io.renren.zwallet.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWalletTxnManager {

    @Resource
    private JCardDao jCardDao;
    @Resource
    private JWalletTxnDao jWalletTxnDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JWalletConfigDao jWalletConfigDao;
    @Resource
    private ChannelFactory channelFactory;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JCardManager jCardManager;

    // 钱包详情
    public WalletInfo walletInfo(JWalletEntity entity) {
        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setHkdLevel(entity.getHkdLevel());
        walletInfo.setUsdLevel(entity.getUsdLevel());

        JBalanceEntity walletAccountHkd = ledgerUtil.getWalletAccount(entity.getId(), "HKD");
        JBalanceEntity walletAccountUsd = ledgerUtil.getWalletAccount(entity.getId(), "USD");

        walletInfo.setHkdBalance(walletAccountHkd.getBalance());
        walletInfo.setUsdBalance(walletAccountUsd.getBalance());

        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, entity.getSubId())
        );

        BigDecimal estimate = walletInfo.getHkdBalance()
                .divide(jWalletConfigEntity.getHkdRate(), 2, RoundingMode.HALF_UP)
                .add(walletInfo.getUsdBalance());

        walletInfo.setUsdEstimate(estimate);
        return walletInfo;
    }

    // 卡信息
    public WalletCardInfo walletCardInfo(JWalletEntity walletEntity) {
        Map<String, List<JCardEntity>> collect = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getWalletId, walletEntity.getId())
                .select(JCardEntity::getId,
                        JCardEntity::getCardno,
                        JCardEntity::getCurrency,
                        JCardEntity::getBalance,
                        JCardEntity::getCvv,
                        JCardEntity::getExpiredate,
                        JCardEntity::getName,
                        JCardEntity::getSurname,
                        JCardEntity::getMarketproduct
                )
        ).stream().collect(Collectors.groupingBy(JCardEntity::getCurrency));
        //港币卡
        List<JCardEntity> jCardEntities = collect.get("HKD");
        List<WalletCard> walletHkdCards = new ArrayList<>();
        if (jCardEntities != null && jCardEntities.size() > 0) {
            for (JCardEntity jCardEntity : jCardEntities) {
                WalletCard walletCard = new WalletCard();
                walletCard.setBalance(jCardEntity.getBalance());
                walletCard.setCardno(jCardEntity.getCardno());
                walletCard.setCvv(CommonUtils.decryptSensitiveString(jCardEntity.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "UTF-8"));
                walletCard.setExpiredate(jCardEntity.getExpiredate());
                walletCard.setMarketproduct(jCardEntity.getMarketproduct());
                walletCard.setName(jCardEntity.getName());
                walletCard.setSurname(jCardEntity.getSurname());
                walletHkdCards.add(walletCard);
            }
        }
        // 美元钱包 + 美元卡
        List<WalletCard> walletUsdCards = new ArrayList<>();
        List<JCardEntity> jCardUsdEntities = collect.get("USD");
        if (jCardUsdEntities != null && jCardUsdEntities.size() > 0) {
            for (JCardEntity jCardEntity : jCardUsdEntities) {
                WalletCard walletCard = new WalletCard();
                walletCard.setBalance(jCardEntity.getBalance());
                walletCard.setCardno(jCardEntity.getCardno());
                walletCard.setCvv(CommonUtils.decryptSensitiveString(jCardEntity.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "UTF-8"));
                walletCard.setExpiredate(jCardEntity.getExpiredate());
                walletCard.setMarketproduct(jCardEntity.getMarketproduct());
                walletCard.setName(jCardEntity.getName());
                walletCard.setSurname(jCardEntity.getSurname());
                walletUsdCards.add(walletCard);
            }
        }
        WalletCardInfo walletCardInfo = new WalletCardInfo();
        walletCardInfo.setHkdCardList(walletHkdCards);
        walletCardInfo.setUsdCardList(walletUsdCards);
        return walletCardInfo;
    }

    // 钱包账户升级(USD|HKD)
    public void upgrade(JCardDTO dto, JWalletEntity walletEntity) {
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        JWalletConfigEntity walletConfig = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, walletEntity.getSubId())
        );

        // 创建账户升级交易
        createUpgradeTxn(walletConfig, walletEntity, dto.getCurrency());

        // 创建主卡
        JCardEntity entity = ConvertUtils.sourceToTarget(dto, JCardEntity.class);
        entity.setApi(0);
        entity.setMeraplid(CommonUtils.uniqueId());
        entity.setTxnid(CommonUtils.uniqueId());
        entity.setSubId(walletEntity.getSubId());
        entity.setWalletId(walletEntity.getId());
        entity.setWalletName(walletEntity.getEmail());
        entity.setMarketproduct(ZinConstant.MP_VPA_WALLET);
        jCardManager.save(entity);

        // 提交发卡行
        jCardManager.submit(entity);
    }

    // 创建账户升级交易
    private void createUpgradeTxn(JWalletConfigEntity walletConfig, JWalletEntity walletEntity, String currency) {
        BigDecimal payAmount = walletConfig.getUpgradeFee();  // 美金计费的
        BigDecimal stlAmount = null;
        if (currency.equals("HKD")) {
            stlAmount = payAmount.multiply(walletConfig.getHkdRate()).setScale(2, RoundingMode.HALF_UP);
        } else {
            stlAmount = payAmount;
        }
        // 创建钱包交易
        JWalletTxnEntity txnEntity = new JWalletTxnEntity();
        fillTxn(txnEntity, walletEntity);
        txnEntity.setTxnCode(ZinConstant.WALLET_TXN_UPGRADE);
        txnEntity.setPayCurrency(currency);
        txnEntity.setPayAmount(payAmount);
        txnEntity.setCurrency(currency);
        txnEntity.setStlAmount(stlAmount);
        txnEntity.setState(ZinConstant.WALLET_TXN_STATUS_NEW);  // 新建
        jWalletTxnDao.insert(txnEntity);
    }

    // 填充交易
    private void fillTxn(JWalletTxnEntity txnEntity, JWalletEntity walletEntity) {
        txnEntity.setWalletId(walletEntity.getId());
        txnEntity.setWalletName(walletEntity.getEmail());
        txnEntity.setAgentId(walletEntity.getAgentId());
        txnEntity.setAgentName(walletEntity.getAgentName());
        txnEntity.setMerchantId(walletEntity.getMerchantId());
        txnEntity.setMerchantName(walletEntity.getMerchantName());
        txnEntity.setSubId(walletEntity.getSubId());
        txnEntity.setSubName(walletEntity.getSubName());
    }

    // 钱包充值:
    public WalletChargeResponse chargeWallet(WalletChargeRequest request, JWalletEntity walletEntity) {
        WalletChargeResponse response = new WalletChargeResponse();

        // 计算下金额
        chargeWalletCalculation(request, walletEntity);

        JWalletTxnEntity txnEntity = new JWalletTxnEntity();
        // 填充
        fillTxn(txnEntity, walletEntity);
        txnEntity.setPayCurrency(request.getPayCurrency());
        txnEntity.setPayAmount(request.getPayAmount());
        txnEntity.setTxnCode(ZinConstant.WALLET_TXN_CHARGE);
        txnEntity.setCurrency(request.getCurrency());
        txnEntity.setStlAmount(request.getAmount());
        txnEntity.setState(ZinConstant.WALLET_TXN_STATUS_NEW);  // 新建

        // 路由获取支付渠道
        PayChannel channel = channelFactory.getChannel(walletEntity.getSubId(), request.getPayCurrency(), request.getCurrency());
        JPayChannelEntity config = channel.getConfig();
        txnEntity.setChannelId(config.getId());
        txnEntity.setChannelName(config.getChannelName());

        // 入库
        jWalletTxnDao.insert(txnEntity);

        // 调用支付渠道, 获取支付地址
        String payUrl = channel.charge(txnEntity);
        response.setPayUrl(payUrl);
        response.setId(txnEntity.getId());
        return response;
    }

    // 钱包提现
    public WalletWithdrawResponse withdrawWallet(WalletWithdrawRequest request, JWalletEntity jWalletEntity) {
        throw new RenException("暂不支持, 请联系管理员");
    }

    // 钱包充值预算: 充值1000HKD  需要多少USDT
    public void chargeWalletCalculation(WalletChargeRequest request, JWalletEntity walletEntity) {
        JWalletConfigEntity config = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, walletEntity.getSubId())
        );
        // USDT支付
        if (request.getPayCurrency().equals("USDT")) {
            if (request.getCurrency().equals("HKD")) {
                BigDecimal hkdRate = config.getHkdRate();
                BigDecimal chargeRate = config.getChargeRate();
                log.info("amount: {}, HKD rate: {}, chargeRate: {}", request.getAmount(), hkdRate, chargeRate);
                BigDecimal fee = request.getAmount().multiply(chargeRate);
                BigDecimal total = fee.add(request.getAmount());
                BigDecimal u = total.divide(hkdRate, 2, RoundingMode.HALF_UP);
                request.setPayAmount(u);
                return;
            }
            if (request.getCurrency().equals("USD")) {
                // todo:
            }
        }

        // 美金支付
        if (request.getPayCurrency().equals("HKD")) {
            if (!request.getCurrency().equals("HKD")) {
                throw new RenException("币种错误");
            }
            // todo:
        }

        // 港币支付
        if (request.getPayCurrency().equals("USD")) {
            if (!request.getCurrency().equals("USD")) {
                throw new RenException("币种错误");
            }
            // todo:
        }

        throw new RenException("请求错误");
    }

    // 充U, 充值132.49得到多少HKD
    public void chargeWalletCalculationReverse(WalletChargeRequest request, JWalletEntity walletEntity) {

        // 目前只支持USDT到法币反算
        if (!request.getPayCurrency().equals("USDT")) {
            throw new RenException("参数错误");
        }

        JWalletConfigEntity config = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, walletEntity.getSubId())
        );

        // 港币美金汇率
        BigDecimal hkdRate = config.getHkdRate();

        // 用户充值汇率
        BigDecimal chargeRate = config.getChargeRate();

        // 到账港币
        if (request.getCurrency().equals("HKD")) {
            BigDecimal payAmount = request.getPayAmount();
            BigDecimal amount = payAmount.multiply(hkdRate).divide(BigDecimal.ONE.add(chargeRate), 2, RoundingMode.HALF_UP);
            request.setAmount(amount);
        }
        // 到账美金
        else if (request.getCurrency().equals("USD")) {
            BigDecimal amount = request.getPayAmount();
            request.setPayAmount(amount);
        }
    }


}
