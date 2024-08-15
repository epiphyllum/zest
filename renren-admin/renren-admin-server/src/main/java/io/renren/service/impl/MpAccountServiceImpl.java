package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.dao.MpAccountDao;
import io.renren.dto.MpAccountDTO;
import io.renren.entity.MpAccountEntity;
import io.renren.service.MpAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 公众号账号管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class MpAccountServiceImpl extends CrudServiceImpl<MpAccountDao, MpAccountEntity, MpAccountDTO> implements MpAccountService {

    @Override
    public QueryWrapper<MpAccountEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<MpAccountEntity> wrapper = new QueryWrapper<>();

        String name = (String)params.get("name");
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        String appId = (String)params.get("appId");
        wrapper.like(StringUtils.isNotBlank(appId), "app_id", appId);

        return wrapper;
    }

}