package io.renren.zmanager;

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
import io.renren.zbalance.LedgerUtil;
import io.renren.zbalance.ledgers.LedgerAllocation;
import io.renren.zcommon.ZapiConstant;
import io.renren.zcommon.ZestConstant;
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
    public LedgerAllocation ledgerAllocation;
    @Resource
    public LedgerUtil ledgerUtil;


    // 商户VA转子商户VA
    public void handleM2s(JAllocateEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        if (vaAccount == null) {
            throw new RenException("can not find account for merchant: " + entity.getMerchantId());
        }
        if (vaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("账户余额不足, 账户余额:" + vaAccount.getBalance());
        }
        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledgerAllocation.ledgeM2s(entity);
            JBalanceEntity after = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("商户账户余额不足" );
            }
        });
    }

    // 子商户VA转商户VA
    public void handleS2m(JAllocateEntity entity) {
        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (subVaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("子商户账户余额不足, 余额:" + subVaAccount.getBalance());
        }

        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledgerAllocation.ledgeS2m(entity);
            JBalanceEntity after = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("子商户账户余额不足");
            }
        });
    }

    // 资金调度处理: web端调用
    public void handleAllocation(JAllocateDTO dto) {
        // 商户才能调拨资金
        UserDetail user = SecurityUser.getUser();
        if (!ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType()) &&
                !ZestConstant.USER_TYPE_OPERATION.equals(user.getUserType()) &&
                !ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())
        ) {
            throw new RenException("权限不足");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JAllocateEntity entity = ConvertUtils.sourceToTarget(dto, JAllocateEntity.class);

        Long merchantId = null;
        if (ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
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
        if (type.equals(ZapiConstant.ALLOCATE_TYPE_S2M) || type.equals(ZapiConstant.ALLOCATE_TYPE_M2S)) {
            JSubEntity subEntity = jSubDao.selectById(entity.getSubId());
            entity.setSubName(subEntity.getCusname());
        }
        switch (entity.getType()) {
            case ZapiConstant.ALLOCATE_TYPE_M2S:
                handleM2s(entity);
                break;
            case ZapiConstant.ALLOCATE_TYPE_S2M:
                handleS2m(entity);
                break;
        }
    }
}


