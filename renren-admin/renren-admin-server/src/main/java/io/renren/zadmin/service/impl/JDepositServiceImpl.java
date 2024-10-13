package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dto.JDepositDTO;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.service.JDepositService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JDepositServiceImpl extends CrudServiceImpl<JDepositDao, JDepositEntity, JDepositDTO> implements JDepositService {
    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JDepositDTO> page(Map<String, Object> params) {
        IPage<JDepositEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JDepositDTO.class);
    }

    @Override
    public QueryWrapper<JDepositEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JDepositEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        String cardno = (String) params.get("cardno");
        if (StringUtils.isNotBlank(cardno)) {
            wrapper.eq("cardno", cardno);
        }

        return wrapper;
    }

}