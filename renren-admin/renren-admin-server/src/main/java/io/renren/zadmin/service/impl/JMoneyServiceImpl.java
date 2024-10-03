package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.dto.JMoneyDTO;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zadmin.service.JMoneyService;
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

    @Override
    public QueryWrapper<JMoneyEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMoneyEntity> wrapper = new QueryWrapper<>();

        CommonFilter.setFilterMerchant(wrapper, params);

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

//    @Override
//    public void save(JMoneyDTO dto) {
//    }

}