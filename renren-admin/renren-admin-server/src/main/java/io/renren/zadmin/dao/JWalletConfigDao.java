package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JWalletConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_wallet_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@Mapper
public interface JWalletConfigDao extends BaseDao<JWalletConfigEntity> {
	
}