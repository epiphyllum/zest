package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JBalanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* j_balance
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-17
*/
@Mapper
public interface JBalanceDao extends BaseDao<JBalanceEntity> {
    @Select("")
    void chargeFeeToday();
}