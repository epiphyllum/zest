package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JStatEntity;
import io.renren.zadmin.entity.VCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * j_stat
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Mapper
public interface JStatDao extends BaseDao<JStatEntity> {
    // 子商户
    @Select("""
             select
             sum(card_sum) as card_sum,
             sum(charge) as charge,
             sum(deposit) as deposit,
             sum(aip_card_sum) as aip_card_sum,
             sum(aip_charge) as aip_charge,
             sum(aip_deposit) as aip_deposit,
             sum(withdraw)    as withdraw,
             sum(withdraw_charge)  as withdraw_charge ,
             sum(aip_withdraw_charge) as aip_withdraw_charge ,
             sum(total_card) as total_card,
             sum(card_fee)as card_fee,
             sum(aip_card_fee) as aip_card_fee,
             currency,
             stat_date
             from j_stat
             where stat_date >= #{beginDate}
             and sub_id = #{subId}
             group by currency, stat_date
            """)
    List<JStatEntity> selectLastMonthOfSub(@Param("beginDate") Date beginDate, @Param("subId") Long subId);

    // 商户
    @Select("""
             select
             sum(card_sum) as card_sum,
             sum(charge) as charge,
             sum(deposit) as deposit,
             sum(aip_card_sum) as aip_card_sum,
             sum(aip_charge) as aip_charge,
             sum(aip_deposit) as aip_deposit,
             sum(withdraw)    as withdraw,
             sum(withdraw_charge)  as withdraw_charge ,
             sum(aip_withdraw_charge) as aip_withdraw_charge ,
             sum(total_card) as total_card,
             sum(card_fee)as card_fee,
             sum(aip_card_fee) as aip_card_fee,
             currency,
             stat_date
             from j_stat
             where stat_date >= #{beginDate}
             and merchant_id = #{merchantId}
             group by currency, stat_date
            """)
    List<JStatEntity> selectLastMonthOfMerchant(@Param("beginDate") Date beginDate, @Param("merchantId") Long merchantId);


    // 代理
    @Select("""
             select
             sum(card_sum) as card_sum,
             sum(charge) as charge,
             sum(deposit) as deposit,
             sum(aip_card_sum) as aip_card_sum,
             sum(aip_charge) as aip_charge,
             sum(aip_deposit) as aip_deposit,
             sum(withdraw)    as withdraw,
             sum(withdraw_charge)  as withdraw_charge ,
             sum(aip_withdraw_charge) as aip_withdraw_charge ,
             sum(total_card) as total_card,
             sum(card_fee)as card_fee,
             sum(aip_card_fee) as aip_card_fee,
             currency,
             stat_date
             from j_stat
             where stat_date >= #{beginDate}
             and agent_id = #{agentId}
             group by currency, stat_date, agent_id
            """)
    List<JStatEntity> selectLastMonthOfAgent(@Param("beginDate") Date beginDate, @Param("agentId") Long agentId);

    //  机构
    @Select("""
             select
             sum(card_sum) as card_sum,
             sum(charge) as charge,
             sum(deposit) as deposit,
             sum(aip_card_sum) as aip_card_sum,
             sum(aip_charge) as aip_charge,
             sum(aip_deposit) as aip_deposit,
             sum(withdraw)    as withdraw,
             sum(withdraw_charge)  as withdraw_charge ,
             sum(aip_withdraw_charge) as aip_withdraw_charge ,
             sum(total_card) as total_card,
             sum(card_fee)as card_fee,
             sum(aip_card_fee) as aip_card_fee,
             currency,
             stat_date
             from j_stat
             where stat_date >= #{beginDate}
             group by currency, stat_date
            """)

    List<JStatEntity> selectLastMonthOfOperation(@Param("beginDate") Date beginDate);

    //  子商户
    @Select("""
             select
             sum(settleamount) as settleamount,
             sum(settlecount) as settlecount,
             currency
             
             from j_stat
             where sub_id = #{subId}
             group by currency
            """)
    List<JStatEntity> selectSumOfSub(@Param("subId") Long subId);

    //  商户
    @Select("""
             select
             sum(settleamount) as settleamount,
             sum(settlecount) as settlecount,
             currency
             from j_stat
             where merchant_id = #{merchantId}
             group by currency
            """)
    List<JStatEntity> selectSumOfMerchant(@Param("merchantId") Long merchantId);

    //  代理
    @Select("""
             select
             sum(settleamount) as settleamount,
             sum(settlecount) as settlecount,
             currency
             from j_stat
             where agent_id = #{agentId}
             group by currency
            """)
    List<JStatEntity> selectSumOfAgent(@Param("agentId") Long agentId);

    //  机构
    @Select("""
             select
             sum(settleamount) as settleamount,
             sum(settlecount) as settlecount,
             currency
             from j_stat
             group by currency
            """)
    List<JStatEntity> selectSumOfOperation();

}