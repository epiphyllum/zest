package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dto.JDepositDTO;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.service.JDepositService;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public QueryWrapper<JDepositEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JDepositEntity> wrapper = new QueryWrapper<>();


        return wrapper;
    }


}