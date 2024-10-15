package io.renren.zapi.cardapply;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.cardapply.dto.*;

import io.renren.zin.cardapply.dto.TCardApplyQuery;
import io.renren.zin.cardapply.dto.TCardApplyResponse;
import io.renren.zin.cardapply.dto.TCardSubApplyRequest;
import io.renren.zin.cardapply.dto.TCardSubApplyResponse;
import io.renren.zin.cardstate.ZinCardStateService;
import io.renren.zin.cardstate.dto.TCardActivateRequest;
import io.renren.zin.cardstate.dto.TCardActivateResponse;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zmanager.JCardManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class ApiCardApplyService {

    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStateService zinCardStateService;

    @Resource
    private JCardManager jCardManager;
    @Resource
    private JCardDao jCardDao;

    // 开卡
    public Result<CardNewRes> cardNew(CardNewReq request, ApiContext context) {
        JMerchantEntity merchant = context.getMerchant();

        // 保存卡
        JCardEntity entity = ConvertUtils.sourceToTarget(request, JCardEntity.class);
        entity.setApi(1);
        entity.setMerchantId(merchant.getId());
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
        // 查询原是卡信息
        JCardEntity entity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(request.getApplyid() != null, JCardEntity::getApplyid, request.getApplyid())
                .eq(request.getMeraplid() != null, JCardEntity::getMeraplid, request.getMeraplid())
        );
        if(entity == null) {
            throw new RenException("no record");
        }

        // 查询通联
        jCardManager.query(entity, false);

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
        JMerchantEntity merchant = context.getMerchant();
        if (!entity.getMerchantId().equals(merchant.getId())) {
            throw new RenException("in valid card number");
        }

        jCardManager.activateCard(entity);

        // 空对象
        CardNewActivateRes res = new CardNewActivateRes();
        return new Result<CardNewActivateRes>().ok(res);
    }
}
