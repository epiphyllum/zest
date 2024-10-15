package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JPacketDao;
import io.renren.zadmin.dto.JPacketDTO;
import io.renren.zadmin.entity.JPacketEntity;
import io.renren.zadmin.service.JPacketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_packet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-13
 */
@Service
public class JPacketServiceImpl extends CrudServiceImpl<JPacketDao, JPacketEntity, JPacketDTO> implements JPacketService {

    @Override
    public PageData<JPacketDTO> page(Map<String, Object> params) {
        IPage<JPacketEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JPacketDTO.class);
    }

    @Override
    public QueryWrapper<JPacketEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JPacketEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);
        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String reqId = (String)params.get("reqId");
        wrapper.eq(StringUtils.isNotBlank(reqId), "req_id", reqId);
        String apiName = (String)params.get("apiName");
        wrapper.eq(StringUtils.isNotBlank(apiName), "api_name", apiName);

        return wrapper;
    }


}