package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.VWithdrawEntity;
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
public interface VWithdrawDao extends BaseDao<VWithdrawEntity> {
    @Select("select * from v_withdraw where stat_date = #{date}")
    List<VWithdrawEntity> selectByDate(@Param("date") Date date);

    // 子商户当日
    @Select("""
            select sum(amount)       as card_sum,
                   sum(fee)          as aip_charge,
                   sum(merchantfee)  as charge,
                   currency 
            from j_withdraw
            where
                state = '04' and
                sub_id = #{subId} and
                stat_date = #{day}
            group by currency 
            """)
    List<VWithdrawEntity> selectDayOfSub(@Param("day") Date day, @Param("subId") Long subId);

    // 商户当日
    @Select("""
            select sum(amount)       as card_sum,
                   sum(fee)          as aip_charge,
                   sum(merchantfee)  as charge,
                   currency 
            from j_withdraw
            where
                state = '04' and
                merchant_id = #{merchantId} and
                stat_date = #{day}
            group by currency 
            """)
    List<VWithdrawEntity> selectDayOfMerchant(@Param("day") Date day, @Param("merchantId") Long merchantId);

    // 代理当日
    @Select("""
            select sum(amount)       as card_sum,
                   sum(fee)          as aip_charge,
                   sum(merchantfee)  as charge,
                   currency 
            from j_withdraw
            where
                state = '04' and
                agent_id = #{agentId} and
                stat_date = #{day}
            group by currency 
            """)
    List<VWithdrawEntity> selectDayOfAgent(@Param("day") Date day, @Param("agentId") Long agentId);

    // 机构当日
    @Select("""
            select sum(amount)       as card_sum,
                   sum(fee)          as aip_charge,
                   sum(merchantfee)  as charge,
                   currency 
            from j_withdraw
            where
                state = '04' and
                stat_date = #{day}
            group by currency 
            """)
    List<VWithdrawEntity> selectDayOfOperation(@Param("day") Date day);

}
