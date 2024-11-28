package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.dto.JWalletDTO;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.service.JWalletService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_wallet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-27
 */
@Service
public class JWalletServiceImpl extends CrudServiceImpl<JWalletDao, JWalletEntity, JWalletDTO> implements JWalletService {

    @Override
    public QueryWrapper<JWalletEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JWalletEntity> wrapper = new QueryWrapper<>();

        String subId = (String)params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);

        return wrapper;
    }


}