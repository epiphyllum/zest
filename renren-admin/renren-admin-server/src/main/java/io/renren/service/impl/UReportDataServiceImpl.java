/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.dao.UReportDataDao;
import io.renren.dto.UReportDataDTO;
import io.renren.entity.UReportDataEntity;
import io.renren.service.UReportDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class UReportDataServiceImpl extends CrudServiceImpl<UReportDataDao, UReportDataEntity, UReportDataDTO> implements UReportDataService {

    @Override
    public QueryWrapper<UReportDataEntity> getWrapper(Map<String, Object> params){
        String fileName = (String)params.get("fileName");

        QueryWrapper<UReportDataEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(fileName), "file_name", fileName);
        return wrapper;
    }

}
