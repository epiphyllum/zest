package io.renren.zwallet.manager;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.redis.RedisUtils;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.dto.JWalletDTO;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.BalanceType;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.*;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletConfigInfo;
import io.renren.zwallet.dto.WalletLoginRequest;
import io.renren.zwallet.scan.TronApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWalletUserManager {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private TronApi tronApi;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JWalletConfigDao jWalletConfigDao;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private MailService mailService;
    @Resource
    private JWalletDao jWalletDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JCardDao jCardDao;
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

    // 保存钱包
    public void save(JWalletEntity walletEntity) {
        fillBySub(walletEntity);
        jWalletDao.insert(walletEntity);
    }

    // 发送otp
    public void emailOTP(String email) {
        String otp = genOTP();
        String domain = CommonUtils.getDomain();

        if (zestConfig.isDev()) {
            log.info("发送OTP:{} -> {}", email, otp);
        }
        JWalletConfigEntity config = getConfigByDomain(domain);
        redisUtils.set(email, otp, 300);
        mailService.sendMail(config.getSubId(), email, "subject", "content");
    }

    // 生成OTP
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

    // 创建虚拟账户
    private void createBalances(String email, Long walletId) {
        // 港币账户
        ledgerUtil.newBalance(
                ZestConstant.USER_TYPE_WALLET, email, walletId,
                BalanceType.getWalletAccount("HKD"), "HKD"
        );
        // 美元账户
        ledgerUtil.newBalance(
                ZestConstant.USER_TYPE_WALLET, email, walletId,
                BalanceType.getWalletAccount("USD"), "USD"
        );
    }

    // 注册
    public String register(WalletLoginRequest request) {
        // 不是测试环境才需要验证otp
        if (!zestConfig.isDev()) {
            String savedOTP = (String) redisUtils.get(request.getEmail());
            if (!request.getOtp().equals(savedOTP)) {
                throw new RenException("验证码错误");
            }
        }

        JWalletEntity walletEntity = new JWalletEntity();
        walletEntity.setEmail(request.getEmail());
        walletEntity.setPassword(DigestUtil.md5Hex(request.getPassword()));

        // 设置ID
        fillByDomain(walletEntity);

        // 配置信息
        JWalletConfigEntity config = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, walletEntity.getSubId())
        );

        // 创建USDT钱包
        TronApi.Account account = tronApi.create(config);
        walletEntity.setUsdtTrc20Key(account.getKey());           // 私钥
        walletEntity.setUsdtTrc20Address(account.getAddress());   // 地址
        walletEntity.setUsdtTrc20Fetch(new Date());               // 最近同步时间
        walletEntity.setUsdtTrc20Ts(1L);                          // 链上最近时间

        // 推荐码
        Long refcode = redisUtils.hInc("refcode:" + walletEntity.getMerchantId(), walletEntity.getSubId().toString(), 1L);
        walletEntity.setRefcode(refcode.toString());

        // 上级
        JWalletEntity parent = jWalletDao.selectOne(Wrappers.<JWalletEntity>lambdaQuery()
                .eq(JWalletEntity::getRefcode, request.getRefcode())
        );
        if (parent != null) {
            walletEntity.setP1(parent.getId());
            walletEntity.setP2(parent.getP1());
        }

        // 上上级
        JWalletEntity grandParent;
        if (walletEntity.getP2() != null) {
            grandParent = jWalletDao.selectById(walletEntity.getP2());
        } else {
            grandParent = null;
        }

        // 创建钱包
        try {
            tx.executeWithoutResult(status -> {

                // 插入
                jWalletDao.insert(walletEntity);

                // 开设账户
                createBalances(request.getEmail(), walletEntity.getId());

                // 上上级增加了二级推荐数
                if (grandParent != null) {
                    jWalletDao.update(null, Wrappers.<JWalletEntity>lambdaUpdate()
                            .eq(JWalletEntity::getId, grandParent.getId())
                            .set(JWalletEntity::getVersion, grandParent.getVersion())
                            .set(JWalletEntity::getVersion, grandParent.getVersion() + 1)
                            .set(JWalletEntity::getS2Count, grandParent.getS2Count() + 1)
                    );
                }
                // 上级增加了一级推荐数
                if (parent != null) {
                    jWalletDao.update(null, Wrappers.<JWalletEntity>lambdaUpdate()
                            .eq(JWalletEntity::getId, parent.getId())
                            .set(JWalletEntity::getVersion, parent.getVersion())
                            .set(JWalletEntity::getVersion, parent.getVersion() + 1)
                            .set(JWalletEntity::getS2Count, parent.getS2Count() + 1)
                    );
                }
            });
        } catch (DuplicateKeyException e) {
            throw new RenException("邮箱已被注册");
        }
        return JwtUtil.genToken(walletEntity);
    }

    // 工具域名, 获取所属子商户的配置
    private JWalletConfigEntity getConfigByDomain(String domain) {
        JWalletConfigEntity config = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getDomain, domain)
        );
        return config;
    }

    // 依据域名填充钱包ID相关信息
    private void fillByDomain(JWalletEntity entity) {
        String domain = CommonUtils.getDomain();
        log.info("访问域名:{}", domain);
        JWalletConfigEntity config = getConfigByDomain(domain);
        entity.setAgentId(config.getAgentId());
        entity.setAgentName(config.getAgentName());
        entity.setMerchantName(config.getMerchantName());
        entity.setMerchantId(config.getMerchantId());
        entity.setSubName(config.getSubName());
        entity.setSubId(config.getSubId());
    }

    // 登录
    public String login(WalletLoginRequest request) {
        if (!zestConfig.isDev()) {
            String savedOTP = (String) redisUtils.get(request.getEmail());
            if (!request.getOtp().equals(savedOTP)) {
                throw new RenException("验证码错误");
            }
        }

        JWalletEntity walletEntity = jWalletDao.selectOne(
                Wrappers.<JWalletEntity>lambdaQuery().eq(JWalletEntity::getEmail, request.getEmail())
        );
        if (walletEntity == null) {
            throw new RenException("用户不存在:" + request.getEmail());
        }

        String hashPass = DigestUtil.md5Hex(request.getPassword());
        if (!hashPass.equals(walletEntity.getPassword())) {
            throw new RenException("密码错误");
        }

        String token = JwtUtil.genToken(walletEntity);
        return token;
    }

    // 修改密码
    public void change(String newPass, String otp) {
        JWalletEntity user = WalletLoginInterceptor.walletUser();
        String savedOTP = (String) redisUtils.get(user.getEmail());
        if (!otp.equals(savedOTP)) {
            throw new RenException("验证码错误");
        }
        String hashPass = DigestUtil.md5Hex(newPass);
        JWalletEntity update = new JWalletEntity();
        update.setId(user.getId());
        update.setPassword(hashPass);
        jWalletDao.updateById(update);
    }

    // 重置密码
    public void reset(String email) {
        JWalletEntity walletEntity = jWalletDao.selectOne(Wrappers.<JWalletEntity>lambdaQuery()
                .eq(JWalletEntity::getEmail, email)
        );
        if (walletEntity == null) {
            throw new RenException("用户不存在");
        }

        String newPass = "";
        String hashPass = DigestUtil.md5Hex(newPass);

        jWalletDao.update(null, Wrappers.<JWalletEntity>lambdaUpdate()
                .eq(JWalletEntity::getId, walletEntity.getId())
                .set(JWalletEntity::getPassword, hashPass)
        );
    }

    // 钱包各个资产余额
    public void attachBalance(List<JWalletDTO> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<Long> ownerIdList = list.stream().map(JWalletDTO::getId).toList();
        Map<Long, List<JBalanceEntity>> collect = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                .in(JBalanceEntity::getOwnerId, ownerIdList)
        ).stream().collect(Collectors.groupingBy(JBalanceEntity::getOwnerId));
        for (JWalletDTO jWalletDTO : list) {
            for (JBalanceEntity jBalanceEntity : collect.get(jWalletDTO.getId())) {
                if (jBalanceEntity.getCurrency().equals("HKD")) {
                    jWalletDTO.setHkdBalance(jBalanceEntity.getBalance());
                } else if (jBalanceEntity.getCurrency().equals("USD")) {
                    jWalletDTO.setUsdBalance(jBalanceEntity.getBalance());
                }
            }
        }
    }

    // 接入密钥
    public void setAccessKey(String accessKey, JWalletEntity jWalletEntity) {
        jWalletDao.update(null, Wrappers.<JWalletEntity>lambdaUpdate()
                .eq(JWalletEntity::getId, jWalletEntity.getId())
                .set(JWalletEntity::getAccessKey, accessKey)
        );
    }

    //  全局配置
    public WalletConfigInfo walletConfigInfo(JWalletEntity jWalletEntity) {
        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, jWalletEntity.getSubId())
        );
        return ConvertUtils.sourceToTarget(jWalletConfigEntity, WalletConfigInfo.class);
    }
}
