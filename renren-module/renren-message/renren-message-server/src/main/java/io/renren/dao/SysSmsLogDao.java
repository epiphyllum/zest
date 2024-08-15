package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.SysSmsLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysSmsLogDao extends BaseDao<SysSmsLogEntity> {
	
}