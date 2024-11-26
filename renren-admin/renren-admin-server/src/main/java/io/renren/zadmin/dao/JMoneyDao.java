package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JMoneyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@Mapper
public interface JMoneyDao extends BaseDao<JMoneyEntity> {

    // 日入金数据: 商户
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            stat_date,
            merchant_id
            from j_money
            where state = 'LG'
            and merchant_id = #{merchantId}
            group by stat_date,
            merchant_id,
            currency
            """)
    List<JMoneyEntity> selectByDateOfMerchant(@Param("date") Date date, @Param("merchantId") Long merchantId);

    // 日入金数据: 代理
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            stat_date,
            merchant_id
            from j_money
            where state = 'LG'
            and agent_id = #{agentId}
            group by stat_date,
            merchant_id,
            currency
            """)
    List<JMoneyEntity> selectByDateOfAgent(@Param("date") Date date, @Param("agentId") Long agentId);

    // 日入金数据: 机构
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            stat_date,
            merchant_id
            from j_money
            where state = 'LG'
            group by stat_date,
            merchant_id,
            currency
            """)
    List<JMoneyEntity> selectByDateOfOperation(@Param("date") Date date);

    // 月入金数据: 商户
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            stat_date
            from j_money
            where state = 'LG'
            and merchant_id = #{merchantId}
            group by stat_date,
            currency
            """)
    List<JMoneyEntity> selectLatestOfMerchant(@Param("beginDate") Date beginDate, @Param("merchantId") Long merchantId);

    // 月入金数据: 代理
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            stat_date
            from j_money
            where state = 'LG'
            and agent_id= #{agentId}
            group by stat_date,
            currency
            """)
    List<JMoneyEntity> selectLatestOfAgent(@Param("beginDate") Date beginDate, @Param("agentId") Long agentId);

    // 月入金数据: 机构
    @Select("""
            select 
            sum(amount) as amount,
            count(1) as id,
            currency,
            stat_date
            from j_money
            where state = 'LG'
            group by stat_date,
            currency
            """)
    List<JMoneyEntity> selectLatestOfOperation(@Param("beginDate") Date beginDate);
}