package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface ProductDao extends BaseDao<ProductEntity> {
	
}