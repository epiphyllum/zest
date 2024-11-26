package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.VDepositEntity;
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
public interface VDepositDao extends BaseDao<VDepositEntity> {

    @Select("""
            select * from v_deposit where stat_date = #{date}
            """)
    List<VDepositEntity> selectByDate(@Param("date") Date date);

    // 子商户当日
    @Select("""
            select sum(merchant_charge)  as charge,
                   sum(merchant_deposit) as deposit,
                   sum(fee)              as aip_charge,
                   sum(securityamount)   as aip_deposit,
                   sum(amount)           as card_sum,
                   sum(txn_amount)       as aip_card_sum,
                   currency 
            from j_deposit
            where stat_date = #{day} and
            state = '04' and 
            sub_id = #{subId}
            group by currency 
            """)
    List<VDepositEntity> selectDayOfSub(@Param("day") Date day, @Param("subId") Long subId);

    // 商户当日
    @Select("""
            select sum(merchant_charge)  as charge,
                   sum(merchant_deposit) as deposit,
                   sum(fee)              as aip_charge,
                   sum(securityamount)   as aip_deposit,
                   sum(amount)           as card_sum,
                   sum(txn_amount)       as aip_card_sum,
                   currency 
            from j_deposit
            where stat_date = #{day} and
            state = '04' and 
            merchant_id = #{merchantId}
            group by currency 
            """)
    List<VDepositEntity> selectDayOfMerchant(@Param("day") Date day, @Param("merchantId") Long merchantId);

    // 代理当日
    @Select("""
            select sum(merchant_charge)  as charge,
                   sum(merchant_deposit) as deposit,
                   sum(fee)              as aip_charge,
                   sum(securityamount)   as aip_deposit,
                   sum(amount)           as card_sum,
                   sum(txn_amount)       as aip_card_sum,
                   currency 
            from j_deposit
            where stat_date = #{day} and
            state = '04' and 
            agent_id = #{agentId}
            group by currency 
            """)
    List<VDepositEntity> selectDayOfAgent(@Param("day") Date day, @Param("agentId") Long agentId);

    // 机构当日
    @Select("""
            select sum(merchant_charge)  as charge,
                   sum(merchant_deposit) as deposit,
                   sum(fee)              as aip_charge,
                   sum(securityamount)   as aip_deposit,
                   sum(amount)           as card_sum,
                   sum(txn_amount)       as aip_card_sum,
                   currency 
            from j_deposit
            where 
            stat_date = #{day} and state = '04'
            group by currency 
            """)
    List<VDepositEntity> selectDayOfOperation(@Param("day") Date day);

}
