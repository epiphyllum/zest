package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.dto.JMoneyDTO;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zadmin.service.JMoneyService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@Service
public class JMoneyServiceImpl extends CrudServiceImpl<JMoneyDao, JMoneyEntity, JMoneyDTO> implements JMoneyService {
    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JMoneyDTO> page(Map<String, Object> params) {
        IPage<JMoneyEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JMoneyDTO.class);
    }

    @Override
    public QueryWrapper<JMoneyEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMoneyEntity> wrapper = new QueryWrapper<>();

        commonFilter.setFilterMerchant(wrapper, params);

        // id
        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq( "id", Long.parseLong(id));
        }

        // 通联账号
        String acctno = (String) params.get("acctno");
        wrapper.like(StringUtils.isNotBlank(acctno), "acctno", acctno);

        // 来账账号名称
        String payeraccountname = (String) params.get("payeraccountname");
        wrapper.like(StringUtils.isNotBlank(payeraccountname), "payeraccountname", payeraccountname);

        // 来账账号
        String payeraccountno = (String) params.get("payeraccountno");
        wrapper.like(StringUtils.isNotBlank(payeraccountno), "payeraccountno", payeraccountno);

        return wrapper;
    }

}