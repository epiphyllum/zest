package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JAuthedEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * j_authed
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-11
 */
@Mapper
public interface JAuthedDao extends BaseDao<JAuthedEntity> {

    @Select("""
            select * from j_authed where entrydate = #{dateStr}
            """)
    List<JAuthedEntity> selectByDate(@Param("dateStr") String dateStr);

    // 当日统计数据 -
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', entryamount, 0)) -
                               -sum(if(trxcode = 'Auth' && trxdir = '101013', entryamount, 0)) 
                               -sum(if(trxcode = 'ReturnOL' && trxdir = '101013', entryamount, 0)) 
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
                        from j_authed
                        where state = '00' and
                        date(trxtime) = #{day}
            group by agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, currency, marketproduct;
                        """)
    JAuthedEntity selectDay(@Param("day") Date day);

    // 当日统计数据 - 子商户
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', entryamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', entryamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', entryamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_authed
                        where state = '00' and
                        sub_id = #{subId} and
                        date(trxtime) >= #{beginDate}
            group by currency
                        """)
    List<JAuthedEntity> selectLatestOfSub(@Param("beginDate") Date beginDate, @Param("subId") Long subId);

    // 最近统计数据 - 商户
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', entryamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', entryamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', entryamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_authed
                        where state = '00' and
                        merchant_id = #{merchantId} and
                        date(trxtime) >= #{beginDate}
            group by currency
                        """)
    List<JAuthedEntity> selectLatestOfMerchant(@Param("beginDate") Date beginDate, @Param("merchantId") Long merchantId);

    // 最近统计 - 代理
    @Select("""
                               select sum(if(trxcode = 'Auth' && trxdir = '101014', entryamount, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', entryamount, 0)) -
                               sum(if(trxcode = 'Auth' && trxdir = '101013', entryamount, 0)) as settleamount,
                               
                               sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                               sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                               
                               currency
                        from j_authed
                        where state = '00' and
                        agent_id = #{agentId} and
                        date(trxtime) >= #{beginDate}
            group by currency
                        """)
    List<JAuthedEntity> selectLatestOfAgent(@Param("beginDate") Date beginDate, @Param("agentId") Long agentId);

    // 最近统计数据 - 机构
    @Select("""
            select sum(if(trxcode = 'Auth' && trxdir = '101014', entryamount, 0)) -
                   sum(if(trxcode = 'ReturnOL' && trxdir = '101013', entryamount, 0)) -
                   sum(if(trxcode = 'Auth' && trxdir = '101013', entryamount, 0)) as settleamount,
                   sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
                   sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) as id,
                   currency
                   from
                       j_authed
                   where 
                       state = '00' and trxtime >= #{beginDate}
            group by currency
            """
    )
    List<JAuthedEntity> selectLatestOfOperation(@Param("beginDate") Date beginDate);

}