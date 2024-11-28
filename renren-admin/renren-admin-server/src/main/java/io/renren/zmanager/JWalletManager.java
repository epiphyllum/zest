package io.renren.zmanager;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.redis.RedisUtils;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zbalance.BalanceType;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.JwtUtil;
import io.renren.zcommon.ZestConstant;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Random;

@Service
public class JWalletManager {
    @Resource
    private JWalletDao jWalletDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private LedgerUtil ledgerUtil;

    // 补充agentId, agentName, merchantName
    public void fillBySub(JWalletEntity entity) {
        Long subId = entity.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());
    }

    public void save(JWalletEntity walletEntity) {
        fillBySub(walletEntity);
        jWalletDao.insert(walletEntity);
    }

    public void emailOTP(String email) {
        String otp = genOTP();
        redisUtils.set(email, otp, 300);
        // todo 发送邮件
    }

    private static String CHARACTERS = "0123456789";

    private String genOTP() {
        StringBuilder randomString = new StringBuilder();
        Random random;
        random = new Random();
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(randomIndex));
        }
        return randomString.toString();
    }

    // 注册
    public void register(String email, String password, String otp) {
        String savedOTP = (String) redisUtils.get(email);
        if (!otp.equals(savedOTP)) {
            throw new RenException("验证码错误");
        }
        JWalletEntity walletEntity = new JWalletEntity();
        walletEntity.setEmail(email);
        walletEntity.setPassword(DigestUtil.md5Hex(password));
        // 设置ID
        fillByDomain(walletEntity);
        walletEntity.setHkdCardno(selectHkdMainCard(walletEntity));
        walletEntity.setUsdCardno(selectUsdMainCard(walletEntity));
        // 创建钱包
        try {
            tx.executeWithoutResult(status -> {
                jWalletDao.insert(walletEntity);
                if (walletEntity.getHkdCardno() != null) {
                    ledgerUtil.newBalance(
                            ZestConstant.USER_TYPE_WALLET, email, walletEntity.getId(),
                            BalanceType.getWalletAccount("HKD"), "HKD"
                    );
                }
                if (walletEntity.getUsdCardno() != null) {
                    ledgerUtil.newBalance(
                            ZestConstant.USER_TYPE_WALLET, email, walletEntity.getId(),
                            BalanceType.getWalletAccount("USD"), "USD"
                    );
                }
            });
        } catch (DuplicateKeyException e) {
            throw new RenException("邮箱已被注册");
        }
    }

    // 港币主卡
    private String selectHkdMainCard(JWalletEntity walletEntity) {
        List<JCardEntity> jCardEntities = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getSubId, walletEntity.getSubId())
                .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_WALLET)
                .eq(JCardEntity::getCurrency, "HKD")
        );
        if (jCardEntities.size() == 0) {
            throw new RenException("卡资源不足");
        }
        int n = new Random().nextInt(jCardEntities.size());
        return jCardEntities.get(n).getCardno();
    }

    // 美元主卡
    private String selectUsdMainCard(JWalletEntity walletEntity) {
        List<JCardEntity> jCardEntities = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getSubId, walletEntity.getSubId())
                .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_WALLET)
                .eq(JCardEntity::getCurrency, "USD")
        );
        if (jCardEntities.size() == 0) {
            throw new RenException("卡资源不足");
        }
        int n = new Random().nextInt(jCardEntities.size());
        return jCardEntities.get(n).getCardno();
    }

    private void fillByDomain(JWalletEntity entity) {
        String domain = CommonUtils.getDomain();
        // todo: 依据域名来找到子商户
        JSubEntity subEntity = jSubDao.selectOne(Wrappers.<JSubEntity>lambdaQuery()
                .eq(JSubEntity::getAddress, domain)
        );
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());
        entity.setSubId(subEntity.getId());
    }

    // 登录
    public String login(String email, String password, String otp) {
        String savedOTP = (String) redisUtils.get(email);
        if (!otp.equals(savedOTP)) {
            throw new RenException("验证码错误");
        }
        JWalletEntity walletEntity = jWalletDao.selectOne(
                Wrappers.<JWalletEntity>lambdaQuery().eq(JWalletEntity::getEmail, email)
        );
        if (walletEntity == null) {
            throw new RenException("用户不存在");
        }
        String hashPass = DigestUtil.md5Hex(password);
        if (!hashPass.equals(walletEntity.getPassword())) {
            throw new RenException("密码错误");
        }

        String token = JwtUtil.genToken(walletEntity);
        return token;
    }

    // 修改密码
    private void changePassword(String email, String newPass, String otp) {
        String savedOTP = (String) redisUtils.get(email);
        if (!otp.equals(savedOTP)) {
            throw new RenException("验证码错误");
        }
        JWalletEntity walletEntity = jWalletDao.selectOne(
                Wrappers.<JWalletEntity>lambdaQuery().eq(JWalletEntity::getEmail, email)
        );
        if (walletEntity == null) {
            throw new RenException("用户不存在");
        }
        String hashPass = DigestUtil.md5Hex(newPass);
        JWalletEntity update = new JWalletEntity();
        update.setId(walletEntity.getId());
        update.setPassword(hashPass);
        jWalletDao.updateById(update);
    }

    private void userInfo(JWalletEntity entity) {
    }
}
