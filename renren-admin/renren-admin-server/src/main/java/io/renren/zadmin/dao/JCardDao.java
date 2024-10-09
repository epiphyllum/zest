package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Mapper
public interface JCardDao extends BaseDao<JCardEntity> {


    // 最近6个月发卡数量(机构) 柱状图
    @Select("")
    void cardStatLast6Month();

    // 最近6个月发卡数量(代理), 柱状图
    @Select("")
    void cardStatLast6MonthByAgent();

    // 最近6个月发卡数量(商户), 柱状图
    @Select("")
    void cardStatLast6MonthByMerchant();


}