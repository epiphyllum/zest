package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JStatEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_stat
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-18
*/
@Mapper
public interface JStatDao extends BaseDao<JStatEntity> {
	
}