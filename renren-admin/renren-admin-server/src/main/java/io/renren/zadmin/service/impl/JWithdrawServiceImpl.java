package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.dto.JWithdrawDTO;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zadmin.service.JWithdrawService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_withdraw
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JWithdrawServiceImpl extends CrudServiceImpl<JWithdrawDao, JWithdrawEntity, JWithdrawDTO> implements JWithdrawService {

    @Override
    public PageData<JWithdrawDTO> page(Map<String, Object> params) {
        IPage<JWithdrawEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JWithdrawDTO.class);
    }

    @Override
    public QueryWrapper<JWithdrawEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JWithdrawEntity> wrapper = new QueryWrapper<>();
        CommonFilter.setFilterAll(wrapper, params);

        String cardno = (String) params.get("cardno");
        if (StringUtils.isNotBlank(cardno)) {
            wrapper.eq("cardno", cardno);
        }

        return wrapper;
    }


}