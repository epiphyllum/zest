package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_log
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-17
*/
@Mapper
public interface JLogDao extends BaseDao<JLogEntity> {
	
}