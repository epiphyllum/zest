package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JAuthedDao;
import io.renren.zadmin.dto.JAuthedDTO;
import io.renren.zadmin.entity.JAuthedEntity;
import io.renren.zadmin.service.JAuthedService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_authed
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-11
 */
@Service
public class JAuthedServiceImpl extends CrudServiceImpl<JAuthedDao, JAuthedEntity, JAuthedDTO> implements JAuthedService {

    @Override
    public QueryWrapper<JAuthedEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JAuthedEntity> wrapper = new QueryWrapper<>();

        String cardno = (String)params.get("cardno");
        wrapper.eq(StringUtils.isNotBlank(cardno), "cardno", cardno);
        String authcode = (String)params.get("authcode");
        wrapper.eq(StringUtils.isNotBlank(authcode), "authcode", authcode);

        return wrapper;
    }


}