package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JChannelLogDao;
import io.renren.zadmin.dto.JChannelLogDTO;
import io.renren.zadmin.dto.JChannelLogDTO;
import io.renren.zadmin.entity.JChannelLogEntity;
import io.renren.zadmin.entity.JChannelLogEntity;
import io.renren.zadmin.service.JChannelLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_channel_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-15
 */
@Service
public class JChannelLogServiceImpl extends CrudServiceImpl<JChannelLogDao, JChannelLogEntity, JChannelLogDTO> implements JChannelLogService {

    @Override
    public PageData<JChannelLogDTO> page(Map<String, Object> params) {
        IPage<JChannelLogEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JChannelLogDTO.class);
    }

    @Override
    public QueryWrapper<JChannelLogEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JChannelLogEntity> wrapper = new QueryWrapper<>();

        String reqId = (String)params.get("reqId");
        wrapper.eq(StringUtils.isNotBlank(reqId), "req_id", reqId);
        String apiName = (String)params.get("apiName");
        wrapper.eq(StringUtils.isNotBlank(apiName), "api_name", apiName);

        return wrapper;
    }


}