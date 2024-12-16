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
import io.renren.zmanager.JExchangeManager;
import io.renren.zwallet.ZWalletConstant;
import io.renren.zwallet.channel.ChannelFactory;
import io.renren.zwallet.channel.SwapChannel;
import io.renren.zwallet.dto.*;
import io.renren.zwallet.scan.TronApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    @Resource
    private JExchangeManager jExchangeManager;
    @Resource
    private TronApi tronApi;

    // 钱包详情
    public WalletInfo walletInfo(JWalletEntity entity) {
        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setHkdLevel(entity.getHkdLevel());
        walletInfo.setUsdLevel(entity.getUsdLevel());

        // 法币账户
        JBalanceEntity walletAccountHkd = ledgerUtil.getWalletAccount(entity.getId(), "HKD");
        JBalanceEntity walletAccountUsd = ledgerUtil.getWalletAccount(entity.getId(), "USD");

        // 余额设置
        walletInfo.setHkdBalance(walletAccountHkd.getBalance());
        walletInfo.setUsdBalance(walletAccountUsd.getBalance());

        // 钱包参数
        JWalletConfigEntity config = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, entity.getSubId())
        );

        // 获取数字货币余额
        attachCoinBalance(walletInfo, entity, config);

        // 换算预估资产
        BigDecimal estimate = walletInfo.getHkdBalance()
                .divide(config.getHkdRate(), 2, RoundingMode.HALF_UP)
                .add(walletInfo.getUsdBalance())
                .add(walletInfo.getUsdtBalance())
                .add(walletInfo.getUsdcBalance());

        walletInfo.setUsdEstimate(estimate);
        return walletInfo;
    }

    private void attachCoinBalance(WalletInfo walletInfo, JWalletEntity entity, JWalletConfigEntity config) {
        CompletableFuture<BigDecimal> usdtFuture = CompletableFuture.supplyAsync(() -> {
            BigDecimal usdtBalance = tronApi.balanceUsdt(config, entity.getUsdtTrc20Address());
            return usdtBalance;
        });
        CompletableFuture<Void> completed = CompletableFuture.allOf(usdtFuture);
        try {
            completed.get();
            BigDecimal usdtBalance = usdtFuture.get();
            walletInfo.setUsdtBalance(usdtBalance);
        } catch (InterruptedException e) {
            throw new RenException("查询失败");
        } catch (ExecutionException e) {
            throw new RenException("查询失败");
        }
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
        // 美元卡
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
        JWalletTxnEntity upgradeTxn = createUpgradeTxn(walletConfig, walletEntity, dto.getCurrency());

        // 查看如何是否足够
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(walletConfig.getId(), dto.getCurrency());
        if (walletAccount.getBalance().compareTo(upgradeTxn.getFromAmount()) < 0) {
            throw new RenException("余额不足");
        }

        // 插入
        jWalletTxnDao.insert(upgradeTxn);

        // 创建主卡
        JCardEntity entity = ConvertUtils.sourceToTarget(dto, JCardEntity.class);
        entity.setApi(0);
        entity.setMeraplid(CommonUtils.uniqueId());
        entity.setTxnid(CommonUtils.uniqueId());
        entity.setSubId(walletEntity.getSubId());
        entity.setWalletId(walletEntity.getId());
        entity.setWalletName(walletEntity.getEmail());
        entity.setMarketproduct(ZinConstant.MP_VPA_WALLET);
        entity.setRelateId(upgradeTxn.getId());
        jCardManager.save(entity);

        // 提交发卡行
        jCardManager.submit(entity);
    }

    // 创建账户升级交易
    private JWalletTxnEntity createUpgradeTxn(JWalletConfigEntity walletConfig, JWalletEntity walletEntity, String currency) {
        // 创建钱包交易
        JWalletTxnEntity txnEntity = new JWalletTxnEntity();
        fillTxn(txnEntity, walletEntity);

        BigDecimal payAmount = walletConfig.getUpgradeFee();  // 美金计费的
        BigDecimal stlAmount;
        if (currency.equals("HKD")) {
            stlAmount = payAmount.multiply(walletConfig.getHkdRate()).setScale(2, RoundingMode.HALF_UP);
        } else {
            stlAmount = payAmount;
        }
        txnEntity.setFromCurrency(currency);
        txnEntity.setFromAmount(stlAmount);
        txnEntity.setToCurrency(currency);
        txnEntity.setToAmount(stlAmount);
        txnEntity.setFee(stlAmount);

        txnEntity.setTxnCode(ZWalletConstant.WALLET_TXN_UPGRADE);
        txnEntity.setState(ZWalletConstant.WALLET_TXN_STATUS_NEW);  // 新建
        return txnEntity;
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

    // 钱包数字充值: 返回地址
    public CoinChargeResponse chargeCoin(CoinChargeRequest request, JWalletEntity walletEntity) {
        CoinChargeResponse response = new CoinChargeResponse();
        if (request.getCurrency().equals("USDT")) {
            if (request.getNetwork().equals("trc20")) {
                response.setAddress(walletEntity.getUsdtTrc20Address());
                return response;
            }
        }
        throw new RenException("暂不支持");
    }

    // 钱包数字货币提现
    public CoinWithdrawResponse withdrawCoin(CoinWithdrawRequest request, JWalletEntity jWalletEntity) {
        throw new RenException("暂不支持, 请联系管理员");
    }

    // 兑换: 数字货币 -> 法币
    public void swap(WalletSwap swap, JWalletEntity walletEntity) {
        JWalletConfigEntity config = jWalletConfigDao.selectOne(
                Wrappers.<JWalletConfigEntity>lambdaQuery()
                        .eq(JWalletConfigEntity::getSubId, walletEntity.getSubId())
        );
        JWalletTxnEntity txnEntity = ConvertUtils.sourceToTarget(swap, JWalletTxnEntity.class);
        fillTxn(txnEntity, walletEntity);
        // usdt -> hkd
        if (swap.getToCurrency().equals("HKD")) {
        }

        SwapChannel channel = channelFactory.getChannel(walletEntity.getSubId(), swap.getFromCurrency(), swap.getToCurrency());
        channel.swap(txnEntity);
    }

    // 换汇: 法币换法币:  HKD <--> USD: todo
    public void exchange(WalletSwap exchange, JWalletEntity walletEntity) {
    }
}
