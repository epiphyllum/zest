package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.VDepositEntity;
import io.renren.zadmin.entity.VWithdrawEntity;
import org.apache.ibatis.annotations.Mapper;
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
    @Select("select * from v_withdraw where stat_date = #{stat_date}")
    List<VWithdrawEntity> selectByDate(Date date);
}
