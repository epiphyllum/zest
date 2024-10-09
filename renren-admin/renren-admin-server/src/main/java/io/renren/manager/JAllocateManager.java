package io.renren.manager;

import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.zadmin.dao.JAllocateDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JAllocateDTO;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Service
public class JAllocateManager {

    @Resource
    private JSubDao jSubDao;
    @Resource
    public JMerchantDao jMerchantDao;
    @Resource
    public JAllocateDao jAllocateDao;
    @Resource
    public TransactionTemplate tx;
    @Resource
    public Ledger ledger;
    @Resource
    public LedgerUtil ledgerUtil;


    // 商户VA转子商户VA
    public void handleM2s(JAllocateEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        if (vaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("VA账户余额不足, VA账户:" + vaAccount.getBalance());
        }
        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledger.ledgeM2s(entity);
            JBalanceEntity after = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("商户VA账户余额不足");
            }
        });
    }

    // 子商户VA转商户VA
    public void handleS2m(JAllocateEntity entity) {

        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (subVaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("子商户VA账户余额不足, VA账户:" + subVaAccount.getBalance());
        }

        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledger.ledgeS2m(entity);
            JBalanceEntity after = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("子商户VA账户余额不足");
            }

        });
    }

    // 资金调度处理
    public void handleAllocation(JAllocateDTO dto) {
        // 商户才能调拨资金
        UserDetail user = SecurityUser.getUser();
        if (!"merchant".equals(user.getUserType()) &&
                !"operation".equals(user.getUserType()) &&
                !"agent".equals(user.getUserType())
        ) {
            throw new RenException("not authorized, you are " + user.getUserType());
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JAllocateEntity entity = ConvertUtils.sourceToTarget(dto, JAllocateEntity.class);

        Long merchantId = null;
        if (user.getUserType().equals("merchant")) {
            merchantId = user.getDeptId();
            entity.setMerchantId(merchantId);
        } else {
            merchantId = dto.getMerchantId();
        }
        if (merchantId == null) {
            throw new RenException("非法请求");
        }
        // 填充代理 + 商户名
        JMerchantEntity merchant = jMerchantDao.selectById(dto.getMerchantId());
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setMerchantName(merchant.getCusname());
        // 非api操作
        entity.setApi(0);

        // 子商户-商户资金调度， 需要子商户信息
        String type = dto.getType();
        if (type.equals("s2m") || type.equals("m2s")) {
            JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
            entity.setSubName(subEntity.getCusname());
        }
        switch (entity.getType()) {
            case "m2s":
                handleM2s(entity);
                break;
            case "s2m":
                handleS2m(entity);
                break;
        }
    }
}


