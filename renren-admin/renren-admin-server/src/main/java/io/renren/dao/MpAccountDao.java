package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.MpAccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 公众号账号管理
*
* @author Mark sunlightcs@gmail.com
*/
@Mapper
public interface MpAccountDao extends BaseDao<MpAccountEntity> {
	
}