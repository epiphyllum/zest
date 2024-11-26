package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JBalanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * j_balance
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Mapper
public interface JBalanceDao extends BaseDao<JBalanceEntity> {

    // 子商户余额
    @Select("""
            select balance,
            balance_type,
            currency
            from j_balance
            where owner_id=#{ownerId}
            """)
    List<JBalanceEntity> selectBalanceOfSub(@Param("subId") Long subId);

    // 制定owner的分类汇总
    @Select("""
            <script>
            select sum(balance) as balance,
            balance_type,
            currency
            from j_balance
            where owner_id in 
            <foreach collection='ownerIdList' index='index' item='item' open='(' close=')' separator=','>
                #{item}
            </foreach>
            group by currency, balance_type
            </script>
            """
    )
    List<JBalanceEntity> selectBalanceOfOwners(@Param("ownerIdList") List<Long> ownerIdList);

    // 全部账户
    @Select(""" 
            select sum(balance) as balance,
            balance_type,
            currency
            from j_balance
            group by currency, balance_type
            """)
    List<JBalanceEntity> selectBalance();
}