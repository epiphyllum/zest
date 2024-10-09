package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-09
*/
@Mapper
public interface JConfigDao extends BaseDao<JConfigEntity> {
	
}