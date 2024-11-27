package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JAllocateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * j_inout
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Mapper
public interface JAllocateDao extends BaseDao<JAllocateEntity> {

    // 每日出入金数据: type: m2s  s2m
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            type
            from j_allocate
            where sub_id = #{subId} and stat_date = #{date}
            group by
            currency,
            type
            """)
    List<JAllocateEntity> selectByDateOfSub(@Param("date") Date date, @Param("subId") Long subId);


    // 每日出入金数据: type: m2s  s2m
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            type,
            stat_date
            from j_allocate
            where sub_id = #{subId} and stat_date >= #{beginDate}
            group by
            currency,
            type,
            stat_date
            """)
    List<JAllocateEntity> selectLatestOfSub(@Param("beginDate") Date beginDate, @Param("subId") Long subId);
}