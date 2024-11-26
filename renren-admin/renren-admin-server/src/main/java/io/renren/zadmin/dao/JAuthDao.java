package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JAuthEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Mapper
public interface JAuthDao extends BaseDao<JAuthEntity> {

    @Insert("""
            insert into j_auth(
            id, agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, 
            cardno, marketproduct, wallet_id, wallet_name,
            logkv, trxcode, trxdir, state, 
            amount, settleamount,currency, settlecurrency, 
            trxtime, mcc, trxaddr, authcode, create_date)
            values (
            #{id}, #{agentId}, #{agentName}, #{merchantId}, #{merchantName}, #{subId}, #{subName}, 
            #{cardno}, #{marketproduct}, #{wallet_id}, #{wallet_name}
            #{logkv}, #{trxcode}, #{trxdir}, #{state}, 
            #{amount}, #{settleamount}, #{currency}, #{settlecurrency},
            #{trxtime}, #{mcc}, #{trxaddr}, #{authcode}, #{createDate})
            on duplicate key update
            logkv = #{logkv},
            trxcode = #{trxcode},
            trxdir = #{trxdir},
            state = #{state},
            amount = #{amount},
            settleamount = #{settleamount},
            currency = #{currency},
            settlecurrency = #{settlecurrency},
            trxtime = #{trxtime},
            mcc = #{mcc},
            trxaddr = #{trxaddr},
            authcode = #{authcode}
             """)
    void saveOrUpdate(JAuthEntity entity);

    // 当日统计数据 -
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', settleamount, 0)) -
                               -sum(if(trxcode = 'Auth' && trxdir = '101013', settleamount, 0)) 
                               -sum(if(trxcode = 'ReturnOL' && trxdir = '101013', settleamount, 0)) 
                               as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency,
                               marketproduct,
                               merchant_id,
                               merchant_name,
                               sub_id,
                               sub_name,
                               agent_id
                        from j_auth
                        where state = '00' and
                        date(trxtime) = #{today}
            group by agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, currency, marketproduct;
                        """)
    JAuthEntity selectDay(Date today);

    // 当日统计数据 - 子商户
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', settleamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', settleamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', settleamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_auth
                        where state = '00' and
                        sub_id = #{subId} and
                        date(trxtime) = #{day}
            group by currency
                        """)
    List<JAuthEntity> selectDayOfSub(@Param("day") Date today, @Param("subId") Long subId);


    // 当日统计数据 - 商户
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', settleamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', settleamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', settleamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_auth
                        where state = '00' and
                        merchant_id = #{merchantId} and
                        date(trxtime) = #{day}
            group by currency
                        """)
    List<JAuthEntity> selectDayOfMerchant(@Param("day") Date day, @Param("merchantId") Long merchantId);

    // 当日统计数据 - 代理
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', settleamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', settleamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', settleamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_auth
                        where state = '00' and
                        agent_id = #{agentId} and
                        date(trxtime) = #{day}
            group by currency
                        """)
    List<JAuthEntity> selectDayOfAgent(@Param("day") Date day, @Param("agentId") Long agentId);

    // 当日统计数据 - 机构
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', settleamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', settleamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', settleamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_auth
                        where state = '00' and
                        date(trxtime) = #{day}
            group by currency
                        """)
    List<JAuthEntity> selectDayOfOperation(@Param("day") Date day);

}