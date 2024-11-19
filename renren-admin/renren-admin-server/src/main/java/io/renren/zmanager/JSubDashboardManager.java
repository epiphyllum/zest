package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zbalance.BalanceType;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JSubDashboardManager {

    @Data
    public static class PrepaidWallet {
        private String cardno;            // 主卡卡号
        private BigDecimal prepaidQuota;  // 发卡额度
        private BigDecimal prepaidSum;    // 发卡总额
        private Long totalCard;        // 发卡数量
        private BigDecimal balance;       // 可用余额
    }

    @Data
    private static class StatItem {
        private BigDecimal cardSum; // 发卡总额
        private BigDecimal cardFee; // 发卡手续费
        private BigDecimal charge;  // 充值手续费
        private BigDecimal deposit; // 保证金
    }

    @Data
    public static class SubDashboard {
        // 基础统计
        private BigDecimal cardSum; // 发卡总额
        private BigDecimal cardFee; // 发卡手续费
        private BigDecimal charge;  // 充值手续费
        private BigDecimal deposit; // 保证金
        // 预付费钱包
        private List<PrepaidWallet> walletList;  // 预防卡钱包
        // 当月统计:  发卡总额
        private StatItem monthStat;
    }

    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JCardManager jCardManager;

    public SubDashboard subDashboard(Long subId) {
        JSubEntity subEntity = jSubDao.selectById(subId);

        SubDashboard subDashboard = new SubDashboard();

        // 基础统计:
        List<JBalanceEntity> balanceList = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, subId)
                .eq(JBalanceEntity::getCurrency, "HKD")
        );
        for (JBalanceEntity b : balanceList) {
            if (b.getBalanceType().equals(BalanceType.getCardSumAccount("HKD"))) {
                subDashboard.setCardSum(b.getBalance());
            } else if (b.getBalanceType().equals(BalanceType.getCardFeeAccount("HKD"))) {
                subDashboard.setCardFee(b.getBalance());
            } else if (b.getBalanceType().equals(BalanceType.getChargeAccount("HKD"))) {
                subDashboard.setCharge(b.getBalance());
            } else if (b.getBalanceType().equals(BalanceType.getDepositAccount("HKD"))) {
                subDashboard.setDeposit(b.getBalance());
            }
        }

        // 这个子商户的预付费主卡
        List<JCardEntity> ppMainCards = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getSubId, subId)
                .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_PREPAID)
        );
        List<Long> ppCardIdList = ppMainCards.stream().map(JCardEntity::getId).toList();
        Map<Long, JCardEntity> cardEntityMap = ppMainCards.stream().collect(Collectors.toMap(JCardEntity::getId, Function.identity()));

        // 这个子商户的预付费主卡相关账户
        Map<Long, List<JBalanceEntity>> walletMap = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, subId)
                .eq(JBalanceEntity::getCurrency, "HKD")
                .in(JBalanceEntity::getOwnerId, ppCardIdList)
        ).stream().collect(Collectors.groupingBy(JBalanceEntity::getOwnerId));

        // 预付费主卡钱包列表
        List<PrepaidWallet> walletList = new ArrayList<>();
        walletMap.forEach((k, v) -> {
            JCardEntity cardEntity = cardEntityMap.get(k);
            PrepaidWallet wallet = new PrepaidWallet();

            // 可用发卡额度， 发卡总额
            for(JBalanceEntity b : v){
                if (b.getBalanceType().equals(BalanceType.getPrepaidQuotaAccount("HKD"))) {
                    wallet.setCardno(b.getOwnerName());
                    wallet.setPrepaidQuota(b.getBalance());
                }
                else if (b.getBalanceType().equals(BalanceType.getPrepaidSumAccount("HKD"))) {
                    wallet.setPrepaidSum(b.getBalance());
                }
            }
            // 查询实际余额
            jCardManager.balanceCard(cardEntity);
            wallet.setBalance(cardEntity.getBalance());

            // 查询卡的发卡总数
            Long totalCard = jCardDao.selectCount(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getMaincardno, cardEntity.getCardno())
            );
            wallet.setTotalCard(totalCard);

            walletList.add(wallet);
        });

        // 当月统计
        StatItem monthState = new StatItem();


        return subDashboard;
    }
}
