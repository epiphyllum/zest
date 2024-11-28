package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JWalletEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_wallet
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-27
*/
@Mapper
public interface JWalletDao extends BaseDao<JWalletEntity> {
	
}