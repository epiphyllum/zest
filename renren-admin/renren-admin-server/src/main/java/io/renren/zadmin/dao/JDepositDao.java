package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* j_deposit
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-19
*/
@Mapper
public interface JDepositDao extends BaseDao<JDepositEntity> {

    // 当前月充值(机构) 按商户饼图
    @Select("")
    void chargeStatCurrentMonth();

    // 当前月充值(代理), 按商户饼图
    @Select("")
    void chargeStatCurrentMonthByAgent(Long agentId);

}