package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JWalletTxnEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_wallet_txn
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@Mapper
public interface JWalletTxnDao extends BaseDao<JWalletTxnEntity> {
	
}