package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JMerchantEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_merchant
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-18
*/
@Mapper
public interface JMerchantDao extends BaseDao<JMerchantEntity> {
	
}