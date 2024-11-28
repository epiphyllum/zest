package io.renren.zwallet.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zbalance.LedgerUtil;
import io.renren.zwallet.dto.WalletInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JWalletInfoManager {

    @Resource
    private JCardDao jCardDao;
    @Resource
    private JWalletDao jWalletDao;
    @Resource
    private LedgerUtil ledgerUtil;

    public WalletInfo walletInfo(JWalletEntity entity) {
        Map<String, List<JCardEntity>> collect = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getWalletId, entity.getId())
                .select(JCardEntity::getId, JCardEntity::getCardno)
        ).stream().collect(Collectors.groupingBy(JCardEntity::getCurrency));

        WalletInfo walletInfo = new WalletInfo();

        // 港币钱包 + 港币卡
        if (entity.getHkdCardno() != null) {
            JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getId(), "HKD");
            walletInfo.setHkdBalance(walletAccount.getBalance());
            List<JCardEntity> jCardEntities = collect.get("HKD");
            List<WalletInfo.WalletCard> walletCards = new ArrayList<>();
            for (JCardEntity jCardEntity : jCardEntities) {
                WalletInfo.WalletCard walletCard = new WalletInfo.WalletCard();
                walletCard.setBalance(jCardEntity.getBalance());
                walletCard.setCardno(jCardEntity.getCardno());
                walletCards.add(walletCard);
            }
        }

        // 美金钱包 + 美金卡
        if (entity.getUsdCardno() != null) {
            JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getId(), "USD");
            walletInfo.setUsdBalance(walletAccount.getBalance());
            List<JCardEntity> jCardEntities = collect.get("USD");
            List<WalletInfo.WalletCard> walletCards = new ArrayList<>();
            for (JCardEntity jCardEntity : jCardEntities) {
                WalletInfo.WalletCard walletCard = new WalletInfo.WalletCard();
                walletCard.setBalance(jCardEntity.getBalance());
                walletCard.setCardno(jCardEntity.getCardno());
                walletCards.add(walletCard);
            }
        }
        return walletInfo;
    }
}
