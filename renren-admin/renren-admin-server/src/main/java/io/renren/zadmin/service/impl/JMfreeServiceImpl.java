package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JMfreeDao;
import io.renren.zadmin.dto.JMfreeDTO;
import io.renren.zadmin.entity.JMfreeEntity;
import io.renren.zadmin.service.JMfreeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_mfree
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-21
 */
@Service
public class JMfreeServiceImpl extends CrudServiceImpl<JMfreeDao, JMfreeEntity, JMfreeDTO> implements JMfreeService {

    @Override
    public QueryWrapper<JMfreeEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JMfreeEntity> wrapper = new QueryWrapper<>();

        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);

        return wrapper;
    }


}