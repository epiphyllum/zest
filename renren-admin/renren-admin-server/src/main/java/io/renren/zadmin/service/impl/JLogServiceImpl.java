package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.service.SysDeptService;
import io.renren.zadmin.dao.JLogDao;
import io.renren.zadmin.dto.JLogDTO;
import io.renren.zadmin.dto.JLogDTO;
import io.renren.zadmin.entity.JLogEntity;
import io.renren.zadmin.entity.JLogEntity;
import io.renren.zadmin.service.JLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Service
@Slf4j
public class JLogServiceImpl extends CrudServiceImpl<JLogDao, JLogEntity, JLogDTO> implements JLogService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JLogDTO> page(Map<String, Object> params) {
        IPage<JLogEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JLogDTO.class);
    }


    @Override
    public QueryWrapper<JLogEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JLogEntity> wrapper = new QueryWrapper<>();

        commonFilter.setLogBalanceFilter(wrapper, params);

        String ownerType = (String) params.get("ownerType");
        wrapper.eq(StringUtils.isNotBlank(ownerType), "owner_type", ownerType);

        //
        String currency = (String) params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);

        //
        String balanceType = (String) params.get("balanceType");
        if (StringUtils.isNotBlank(balanceType)) {
            if (balanceType.endsWith("_")) {
                if (StringUtils.isNotBlank(currency)) {
                    // 余额类型以"_"结尾, 且有币种: 就等于
                    wrapper.eq("balance_type", balanceType + currency);
                    log.debug("1. use eq: {}", balanceType + currency);
                } else {
                    // 余额类型以"_"结尾, 没有币种: likeRight;
                    wrapper.likeRight("balance_type", balanceType);
                    log.debug("2. use likeRight: {}", balanceType);
                }
            } else {
                // 不以"_"结尾
                wrapper.eq("balance_type", balanceType);
                log.debug("3. use eq: {}", balanceType);
            }
        }

        //
        String originType = (String) params.get("originType");
        wrapper.eq(StringUtils.isNotBlank(originType), "origin_type", originType);

        //
        String factType = (String) params.get("factType");
        wrapper.eq(StringUtils.isNotBlank(factType), "fact_type", factType);

        //
        String factId = (String) params.get("factId");
        wrapper.eq(StringUtils.isNotBlank(factId), "fact_id", factId);

        return wrapper;
    }

}