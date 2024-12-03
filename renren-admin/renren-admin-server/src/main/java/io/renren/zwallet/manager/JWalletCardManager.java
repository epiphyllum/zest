package io.renren.zwallet.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zmanager.JCardManager;
import io.renren.zmanager.JVpaManager;
import io.renren.zwallet.dto.WalletCardChargeRequest;
import io.renren.zwallet.dto.WalletCardWithdrawRequest;
import io.renren.zwallet.dto.WalletCardOpenRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JWalletCardManager {

    @Resource
    private JCardManager jCardManager;
    @Resource
    private JVpaManager jVpaManager;
    @Resource
    private JVpaJobDao jVpaJobDao;
    @Resource
    private JCardDao jCardDao;

    // 发卡
    public Long open(WalletCardOpenRequest request, JWalletEntity walletEntity) {
        JVpaJobEntity job = ConvertUtils.sourceToTarget(request, JVpaJobEntity.class);
        job.setWalletId(walletEntity.getId());
        jVpaManager.save(job);
        JVpaJobEntity savedJob = jVpaJobDao.selectById(job.getId());
        jVpaManager.submit(savedJob);
        return savedJob.getId();
    }

    // 发卡查询
    public void openQuery(Long jobId, JWalletEntity walletEntity) {
    }

    // 充值
    public void charge(WalletCardChargeRequest request, JWalletEntity walletEntity) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );

        // 校验卡号
        if (!cardEntity.getWalletId().equals(walletEntity.getId())) {
            throw new RenException("卡号错误");
        }

        jCardManager.walletCardCharge(cardEntity, request.getAmount());
    }

    // 提现
    public void withdraw(WalletCardWithdrawRequest request, JWalletEntity walletEntity) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );
        // 校验卡号
        if (!cardEntity.getWalletId().equals(walletEntity.getId())) {
            throw new RenException("卡号错误");
        }
        jCardManager.walletCardWithdraw(cardEntity, request.getAmount());
    }

}
