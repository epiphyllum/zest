package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* j_merchant
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-18
*/
@Mapper
public interface JSubDao extends BaseDao<JSubEntity> {
}