package io.renren.dao;


import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.MpMenuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 公众号自定义菜单
*
* @author Mark sunlightcs@gmail.com
*/
@Mapper
public interface MpMenuDao extends BaseDao<MpMenuEntity> {
	
}