package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.VCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
public interface VCardDao extends BaseDao<VCardEntity> {
    @Select("select * from v_card where stat_date = #{stat_date}")
    List<VCardEntity> selectByDate(Date date);

    // 子商户
    @Select("""
            select count(1) as total_card,
                   sum(fee) as fee,
                   sum(merchantfee) as merchantfee,
                   currency
            from
                j_card
            where
                state = '04' and
                sub_id = #{subId} and
                stat_date = #{day}
            group by 
                currency 
            """)
    List<VCardEntity> selectDayOfSub(@Param("day") Date day, @Param("subId") Long subId);

    // 商户
    @Select("""
            select count(1)          as total_card,
                   sum(fee) as fee,
                   sum(merchantfee) as merchantfee,
                   currency
            from
                j_card
            where
                state = '04' and
                merchant_id = #{merchantId} and
                stat_date = #{day}
            group by 
                currency 
            """)
    List<VCardEntity> selectDayOfMerchant(@Param("day") Date day, @Param("merchantId") Long merchantId);

    // 代理
    @Select("""
            select count(1)          as total_card,
                   sum(fee) as fee,
                   sum(merchantfee) as merchantfee,
                   currency
            from j_card
            where 
            state = '04' and 
            agent_id = #{agentId} and 
            stat_date = #{day}
            group by currency 
            """)
    List<VCardEntity> selectDayOfAgent(@Param("day") Date day, @Param("agentId") Long agentId);

    // 机构
    @Select("""
            select count(1)          as total_card,
                   sum(fee) as fee,
                   sum(merchantfee) as merchantfee,
                   currency
            from j_card
            where 
            state = '04' and 
            stat_date = #{day}
            group by currency 
            """)
    List<VCardEntity> selectDayOfOperation(@Param("day") Date day);
}