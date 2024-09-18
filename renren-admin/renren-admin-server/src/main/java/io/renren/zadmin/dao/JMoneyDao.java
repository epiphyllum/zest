package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JMoneyEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* j_money
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-20
*/
@Mapper
public interface JMoneyDao extends BaseDao<JMoneyEntity> {
}