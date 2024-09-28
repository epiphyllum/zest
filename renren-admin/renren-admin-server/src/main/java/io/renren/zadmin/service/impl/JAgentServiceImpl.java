package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.entity.SysDeptEntity;
import io.renren.service.SysDeptService;
import io.renren.zadmin.dao.JAgentDao;
import io.renren.zadmin.dto.JAgentDTO;
import io.renren.zadmin.entity.JAgentEntity;
import io.renren.zadmin.service.JAgentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * j_agent
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.0 2024-08-16
 */
@Service
@Slf4j
public class JAgentServiceImpl extends CrudServiceImpl<JAgentDao, JAgentEntity, JAgentDTO> implements JAgentService {

    @Resource
    private SysDeptService sysDeptService;

    @Override
    public QueryWrapper<JAgentEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JAgentEntity> wrapper = new QueryWrapper<>();
        return wrapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(JAgentDTO dto) {
        UserDetail user = SecurityUser.getUser();

        if ("operation".equals(user.getUserType())) {
            throw new RenException("only operation can add agent");
        }

        // 插入系统sys_dept
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setPid(user.getDeptId());
        deptEntity.setName(dto.getAgentName());
        deptEntity.setPids(user.getDeptId().toString());
        sysDeptService.insert(deptEntity);

        // 插入j_agent
        JAgentEntity jAgentEntity = ConvertUtils.sourceToTarget(dto, JAgentEntity.class);
        jAgentEntity.setId(deptEntity.getId());

        insert(jAgentEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            sysDeptService.delete(id);
            baseDao.deleteById(id);
        }
    }

}