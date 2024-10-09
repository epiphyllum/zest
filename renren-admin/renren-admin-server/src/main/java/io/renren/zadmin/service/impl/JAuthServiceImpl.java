package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dto.JAuthDTO;
import io.renren.zadmin.dto.JAuthDTO;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.service.JAuthService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JAuthServiceImpl extends CrudServiceImpl<JAuthDao, JAuthEntity, JAuthDTO> implements JAuthService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JAuthDTO> page(Map<String, Object> params) {
        IPage<JAuthEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JAuthDTO.class);
    }

    @Override
    public QueryWrapper<JAuthEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JAuthEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        String cardno = (String) params.get("cardno");
        wrapper.eq(StringUtils.isNotBlank(cardno), "cardno", cardno);
        String trxtype = (String) params.get("trxtype");
        wrapper.eq(StringUtils.isNotBlank(trxtype), "trxtype", trxtype);
        String authcode = (String) params.get("authcode");
        wrapper.eq(StringUtils.isNotBlank(authcode), "authcode", authcode);
        return wrapper;
    }


}