package io.renren.zapi.sub;

import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.sub.dto.SubCreate;
import io.renren.zapi.sub.dto.SubCreateRes;
import io.renren.zapi.sub.dto.SubQuery;
import io.renren.zapi.sub.dto.SubQueryRes;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiSubService {

    @Resource
    private JSubDao jSubDao;

    public Result<SubCreateRes> subCreate(SubCreate request, ApiContext context) {
        JMerchantEntity merchant = context.getMerchant();
        JSubEntity subEntity = ConvertUtils.sourceToTarget(request, JSubEntity.class);
        subEntity.setMerchantId(merchant.getId());
        subEntity.setMerchantName(merchant.getCusname());
        subEntity.setAgentId(merchant.getAgentId());
        subEntity.setAgentName(merchant.getAgentName());
        subEntity.setState(ZinConstant.MERCHANT_STATE_TO_VERIFY);
        jSubDao.insert(subEntity);
        SubCreateRes subCreateRes = new SubCreateRes(subEntity.getId());
        Result<SubCreateRes> result = new Result<>();
        result.setData(subCreateRes);
        return result;
    }

    public Result<SubQueryRes> subQuery(SubQuery request, ApiContext context) {
        JSubEntity subEntity = jSubDao.selectById(request.getSubId());
        if (subEntity == null) {
            throw new RenException("no record");
        }

        Result<SubQueryRes> result = new Result<>();
        SubQueryRes res = new SubQueryRes(subEntity.getId(), subEntity.getCusname(), subEntity.getState());
        result.setData(res);
        return result;
    }
}
