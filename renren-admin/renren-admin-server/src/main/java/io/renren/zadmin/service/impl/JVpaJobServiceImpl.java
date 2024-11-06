package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.dto.JVpaJobDTO;
import io.renren.zadmin.dto.JVpaJobDTO;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zadmin.service.JVpaJobService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_vpa_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-01
 */
@Service
public class JVpaJobServiceImpl extends CrudServiceImpl<JVpaJobDao, JVpaJobEntity, JVpaJobDTO> implements JVpaJobService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JVpaJobDTO> page(Map<String, Object> params) {
        IPage<JVpaJobEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JVpaJobDTO.class);
    }

    @Override
    public QueryWrapper<JVpaJobEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JVpaJobEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        String cycle = (String) params.get("cycle");
        wrapper.eq(StringUtils.isNotBlank(cycle), "cycle", cycle);

        String state = (String) params.get("state");
        wrapper.eq(StringUtils.isNotBlank(state), "state", state);

        String maincardno = (String) params.get("maincardno");
        wrapper.eq(StringUtils.isNotBlank(maincardno), "maincardno", maincardno);

        String meraplid = (String) params.get("meraplid");
        wrapper.eq(StringUtils.isNotBlank(meraplid), "meraplid", meraplid);

        String applyid = (String) params.get("applyid");
        wrapper.eq(StringUtils.isNotBlank(applyid), "applyid", applyid);

        return wrapper;
    }


}