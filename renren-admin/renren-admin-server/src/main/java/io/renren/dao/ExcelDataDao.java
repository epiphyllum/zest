package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.ExcelDataEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* Excel导入演示
*
* @author Mark sunlightcs@gmail.com
*/
@Mapper
public interface ExcelDataDao extends BaseDao<ExcelDataEntity> {
	
}