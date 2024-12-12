package io.renren.zwallet.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.DateUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.*;
import io.renren.zwallet.dto.WalletCardChargeRequest;
import io.renren.zwallet.dto.WalletCardOpenRequest;
import io.renren.zwallet.dto.WalletCardWithdrawRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class JWalletCardManager {

    @Resource
    private JDepositManager jDepositManager;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JVpaManager jVpaManager;
    @Resource
    private JVpaJobDao jVpaJobDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JWithdrawManager jWithdrawManager;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JWalletConfigDao jWalletConfigDao;
    @Resource
    private JCommon jCommon;

    private JCardEntity getMainCard(JWalletEntity walletEntity, String currency) {

        // 如果港币账户是高级账户
        if (currency.equals("HKD") && walletEntity.getHkdLevel().equals(ZinConstant.WALLET_LEVEL_PREMIUM)) {
            log.info("使用用户专享HKD主卡");
            return jCardDao.selectById(walletEntity.getHkdCardid());
        }

        // 如果美元账户是高级账户
        if (currency.equals("USD") && walletEntity.getUsdLevel().equals(ZinConstant.WALLET_LEVEL_PREMIUM)) {
            log.info("使用用户专享USD主卡");
            return jCardDao.selectById(walletEntity.getUsdCardid());
        }

        log.info("共享子商户主卡...");
        // 共享子商户的钱包主卡
        List<JCardEntity> jCardEntities = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getSubId, walletEntity.getSubId())
                .eq(JCardEntity::getCurrency, currency)
                .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_WALLET)
                .isNull(JCardEntity::getWalletId)
        );
        // todo: 选择用哪个卡
        return jCardEntities.get(0);
    }

    // 匿名发卡
    public Long openVpa(WalletCardOpenRequest request, JWalletEntity walletEntity) {
        // 钱包业务配置
        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, walletEntity.getSubId())
        );

        // 开卡成本
        JFeeConfigEntity feeConfig = jCommon.getFeeConfig(jWalletConfigEntity.getMerchantId(), ZinConstant.MP_VPA_WALLET, request.getCurrency());

        BigDecimal money = request.getAmount().multiply(new BigDecimal(request.getNum()));
        BigDecimal fee = feeConfig.getCardFee();
        BigDecimal total = money.add(fee);

        // 子商户va
        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(walletEntity.getSubId(), request.getCurrency());
        if (subVaAccount.getBalance().compareTo(total) < 0) {
            log.error("子商户va余额不足, 需要:{}, 只有:{}", total, subVaAccount.getBalance());
            throw new RenException("系统异常, 其请联系管理员");
        }

        // 保存发卡任务
        JVpaJobEntity job = ConvertUtils.sourceToTarget(request, JVpaJobEntity.class);
        job.setWalletId(walletEntity.getId());
        job.setWalletName(walletEntity.getEmail());
        job.setSubId(walletEntity.getSubId());

        // 指定主卡
        JCardEntity mainCard = getMainCard(walletEntity, request.getCurrency());
        job.setMaincardid(mainCard.getId());
        job.setMaincardno(mainCard.getCardno());
        job.setMarketproduct(ZinConstant.MP_VPA_WALLET);

        // 主卡有效期
        String mainExpiredate = mainCard.getExpiredate();
        String year = mainExpiredate.substring(0, 2);
        String month = mainExpiredate.substring(2, 4);

        // 指定token有效期, 起始日期
        String expireDate = "20" + year + "-" + month + "-" + "01";
        job.setCardexpiredate(expireDate);
        job.setEnddate(expireDate);
        job.setBegindate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));

        // 接收邮件: todo
        job.setEmail(walletEntity.getEmail());

        // 期限卡
        job.setCycle(ZinConstant.VPA_CYCLE_DEADLINE);

        // 笔数与额度
        job.setAuthmaxcount(10000000);
        job.setAuthmaxamount(request.getAmount());

        job.setApi(0);
        job.setMeraplid(CommonUtils.uniqueId());

        jVpaManager.save(job);

        // 查询出来后， 提交通联
        JVpaJobEntity savedJob = jVpaJobDao.selectById(job.getId());
        jVpaManager.submit(savedJob);
        return savedJob.getId();
    }

    // 补充agentId, agentName, merchantName
    public void fillBySub(JCardEntity entity) {
        Long subId = entity.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());
    }

    // 实名发卡
    public Long openVcc(JCardEntity card, JWalletEntity wallet) {
        card.setWalletId(wallet.getId());
        card.setSubId(wallet.getSubId());
        fillBySub(card);
        jCardManager.saveVccSub(card);
        return card.getId();
    }

    // 实名卡/实体卡发卡查询
    public String openVccQuery(Long id, JWalletEntity value) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        if (cardEntity != null) {
            throw new RenException("错误");
        }
        if (!cardEntity.getState().equals(ZinConstant.CARD_APPLY_SUCCESS)) {
            jCardManager.query(cardEntity, false);
        }
        return cardEntity.getState();
    }

    // 匿名卡发卡查询
    public String openVpaQuery(Long jobId, JWalletEntity walletEntity) {
        JVpaJobEntity job = jVpaJobDao.selectById(jobId);
        if (!ZinConstant.CARD_APPLY_SUCCESS.equals(job.getState())) {
            jVpaManager.query(job, false);
        }

        // 查询下通联
        return job.getState();  // 返回发卡状态
    }

    // 卡充值
    public void chargeCard(WalletCardChargeRequest request, JWalletEntity walletEntity) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );
        // 校验卡号
        if (!cardEntity.getWalletId().equals(walletEntity.getId())) {
            throw new RenException("卡号错误");
        }

        // 钱包子卡充值
        if (cardEntity.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
            boolean ok = jCardManager.walletCardCharge(cardEntity, request.getAmount());

            // 调整子卡额度成功后, 给主卡充值
            if (ok) {
                JDepositEntity deposit = new JDepositEntity();
                deposit.setAmount(request.getAmount());
                deposit.setCardno(cardEntity.getMaincardno());
                deposit.setSubId(walletEntity.getSubId());
                deposit.setCurrency(cardEntity.getCurrency());
                deposit.setApi(0);
                deposit.setMeraplid(CommonUtils.uniqueId());
                jDepositManager.saveAndSubmit(deposit, true);
                return;
            }

        }

        // 其他卡: todo
        JDepositEntity deposit = new JDepositEntity();
        jDepositManager.saveAndSubmit(deposit, true);
    }

    // 卡提现
    public void withdrawCard(WalletCardWithdrawRequest request, JWalletEntity walletEntity) {
        //
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );

        // 校验卡号
        if (!cardEntity.getWalletId().equals(walletEntity.getId())) {
            throw new RenException("卡号错误");
        }

        // 钱包子卡充值
        if (cardEntity.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
            jCardManager.walletCardWithdraw(cardEntity, request.getAmount());
            return;
        }

        // 统计
        JWithdrawEntity withdrawEntity = new JWithdrawEntity();
        jWithdrawManager.save(withdrawEntity);
        jWithdrawManager.submit(withdrawEntity);
    }


    public BigDecimal balance(String cardno) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, cardno)
        );
        jCardManager.balanceCard(cardEntity);
        return cardEntity.getBalance();
    }
}
