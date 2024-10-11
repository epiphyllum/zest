package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JAuthedEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_authed
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-11
*/
@Mapper
public interface JAuthedDao extends BaseDao<JAuthedEntity> {
	
}