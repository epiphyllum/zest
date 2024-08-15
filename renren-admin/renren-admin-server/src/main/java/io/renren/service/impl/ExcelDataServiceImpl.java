package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.dao.ExcelDataDao;
import io.renren.dto.ExcelDataDTO;
import io.renren.entity.ExcelDataEntity;
import io.renren.service.ExcelDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Excel导入演示
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class ExcelDataServiceImpl extends CrudServiceImpl<ExcelDataDao, ExcelDataEntity, ExcelDataDTO> implements ExcelDataService {

    @Override
    public QueryWrapper<ExcelDataEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<ExcelDataEntity> wrapper = new QueryWrapper<>();

        String realName = (String)params.get("realName");
        wrapper.like(StringUtils.isNotBlank(realName), "real_name", realName);

        String userIdentity = (String)params.get("userIdentity");
        wrapper.like(StringUtils.isNotBlank(userIdentity), "user_identity", userIdentity);

        return wrapper;
    }
}