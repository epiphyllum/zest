package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JVpaJobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_vpa_log
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-01
*/
@Mapper
public interface JVpaJobDao extends BaseDao<JVpaJobEntity> {
	
}