package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.VCardEntity;
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
public interface VCardDao extends BaseDao<VCardEntity> {
    @Select( "select * from v_card where stat_date = #{stat_date}" )
    List<VCardEntity> selectByDate(Date date);
}