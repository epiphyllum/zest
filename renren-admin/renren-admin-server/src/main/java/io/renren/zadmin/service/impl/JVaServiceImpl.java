package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dto.JVaDTO;
import io.renren.zadmin.entity.JVaEntity;
import io.renren.zadmin.service.JVaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_va
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Service
public class JVaServiceImpl extends CrudServiceImpl<JVaDao, JVaEntity, JVaDTO> implements JVaService {

    @Override
    public QueryWrapper<JVaEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JVaEntity> wrapper = new QueryWrapper<>();

        String accountno = (String)params.get("accountno");
        wrapper.eq(StringUtils.isNotBlank(accountno), "accountno", accountno);
        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);

        return wrapper;
    }


}