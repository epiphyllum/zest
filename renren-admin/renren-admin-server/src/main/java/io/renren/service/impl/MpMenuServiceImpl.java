package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.MpMenuDao;
import io.renren.dto.MpMenuDTO;
import io.renren.entity.MpMenuEntity;
import io.renren.service.MpMenuService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 公众号自定义菜单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class MpMenuServiceImpl extends CrudServiceImpl<MpMenuDao, MpMenuEntity, MpMenuDTO> implements MpMenuService {

    @Override
    public QueryWrapper<MpMenuEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<MpMenuEntity> wrapper = new QueryWrapper<>();

        return wrapper;
    }

    @Override
    public MpMenuDTO getByAppId(String appId) {
        MpMenuEntity entity = baseDao.selectOne(new QueryWrapper<MpMenuEntity>().eq("app_id", appId));
        return ConvertUtils.sourceToTarget(entity, MpMenuDTO.class);
    }

    @Override
    public void deleteByAppId(String appId) {
        baseDao.delete(new QueryWrapper<MpMenuEntity>().eq("app_id", appId));
    }
}