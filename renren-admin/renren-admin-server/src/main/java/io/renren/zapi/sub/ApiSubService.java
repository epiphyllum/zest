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
import io.renren.zmanager.JSubManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiSubService {

    @Resource
    private JSubDao jSubDao;

    @Resource
    private JSubManager jSubManager;

    // 创建子商户
    public Result<SubCreateRes> subCreate(SubCreate request, ApiContext context) {
        // 准备数据
        JMerchantEntity merchant = context.getMerchant();
        JSubEntity subEntity = ConvertUtils.sourceToTarget(request, JSubEntity.class);
        subEntity.setMerchantId(merchant.getId());
        subEntity.setMerchantName(merchant.getCusname());
        subEntity.setAgentId(merchant.getAgentId());
        subEntity.setAgentName(merchant.getAgentName());
        subEntity.setState(ZinConstant.MONEY_ACCOUNT_TO_VERIFY);
        subEntity.setApi(1);

        // 调用manager服务
        jSubManager.save(subEntity);

        // 应答
        SubCreateRes subCreateRes = new SubCreateRes(subEntity.getId());
        Result<SubCreateRes> result = new Result<>();
        result.setData(subCreateRes);
        return result;
    }

    // 查询子商户
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
