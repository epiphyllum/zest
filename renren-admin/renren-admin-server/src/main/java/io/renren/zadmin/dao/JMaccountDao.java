package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JMaccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_maccount
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-19
*/
@Mapper
public interface JMaccountDao extends BaseDao<JMaccountEntity> {
	
}