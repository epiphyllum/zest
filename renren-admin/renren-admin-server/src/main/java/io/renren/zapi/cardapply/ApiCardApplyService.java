package io.renren.zapi.cardapply;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.cardapply.dto.*;
import io.renren.zbalance.ledgers.Ledger500OpenCard;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.BankException;
import io.renren.zmanager.JCardManager;
import io.renren.zwallet.ZWalletConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


@Service
@Slf4j
public class ApiCardApplyService {

    @Resource
    private JCardManager jCardManager;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JVpaJobDao jVpaJobDao;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private Ledger500OpenCard ledger500OpenCard;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JWalletTxnDao jWalletTxnDao;

    // 开卡
    public Result<CardNewRes> cardNew(CardNewReq request, ApiContext context) {
        JMerchantEntity merchant = context.getMerchant();

        // 保存卡
        JCardEntity entity = ConvertUtils.sourceToTarget(request, JCardEntity.class);
        entity.setApi(1);
        entity.setMerchantId(merchant.getId());
        entity.setTxnid(CommonUtils.uniqueId());
        jCardManager.save(entity);

        // 提交通联
            jCardManager.submit(entity);


        // 应答商户
        Result<CardNewRes> result = new Result<>();
        CardNewRes cardNewRes = new CardNewRes(entity.getApplyid());
        result.setData(cardNewRes);
        return result;

    }

    // 开卡查询
    public Result<CardNewQueryRes> cardNewQuery(CardNewQuery request, ApiContext context) {
        if (request.getApplyid() == null && request.getMeraplid() == null) {
            throw new RenException("字段applyid,meraplid至少提供一个");
        }

        // 查询原卡信息
        JCardEntity entity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(request.getApplyid() != null, JCardEntity::getApplyid, request.getApplyid())
                .eq(request.getMeraplid() != null, JCardEntity::getMeraplid, request.getMeraplid())
        );
        if (entity == null) {
            throw new RenException("记录不存在");
        }

        // 如果卡申请已经是终态了
        if (ZinConstant.isCardApplySuccess(entity.getState()) ||
                ZinConstant.isCardApplyFail(entity.getState())
        ) {
            log.info("发卡已经是终态");
        } else {
            // 查询通联
            jCardManager.query(entity, false);
        }

        // 应答
        entity = jCardDao.selectById(entity.getId());
        context.info("entity: {}", entity);

        CardNewQueryRes cardNewQueryRes = ConvertUtils.sourceToTarget(entity, CardNewQueryRes.class);
        return new Result<CardNewQueryRes>().ok(cardNewQueryRes);
    }

    // 卡激活
    public Result<CardNewActivateRes> cardNewActivate(CardNewActivateReq request, ApiContext context) {
        JCardEntity entity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, request.getCardno())
        );
        if (entity == null) {
            throw new RenException("卡号不存在:" + request.getCardno());
        }

        JMerchantEntity merchant = context.getMerchant();
        if (!entity.getMerchantId().equals(merchant.getId())) {
            throw new RenException("卡号不属于商户:" + request.getCardno());
        }

        // 激活卡
        jCardManager.activateCardApi(entity, request);

        // 返回空对象
        CardNewActivateRes res = new CardNewActivateRes();
        return new Result<CardNewActivateRes>().ok(res);
    }
}
