package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.VCardEntity;
import io.renren.zadmin.entity.VDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* VIEW
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-18
*/
@Mapper
public interface VDepositDao extends BaseDao<VDepositEntity> {
    @Select( "select * from v_deposit where stat_date = #{stat_date}" )
    List<VDepositEntity> selectByDate(Date date);
}