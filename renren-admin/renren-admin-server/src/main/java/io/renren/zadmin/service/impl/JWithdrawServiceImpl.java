package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.dto.JWithdrawDTO;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zadmin.service.JWithdrawService;
import org.apache.commons.lang3.StringUtils;
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
    public QueryWrapper<JWithdrawEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JWithdrawEntity> wrapper = new QueryWrapper<>();


        return wrapper;
    }


}