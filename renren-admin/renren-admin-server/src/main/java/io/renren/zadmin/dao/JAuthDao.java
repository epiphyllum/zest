package io.renren.zadmin.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.zadmin.entity.JAuthEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Mapper
public interface JAuthDao extends BaseDao<JAuthEntity> {
    @Insert("insert into j_auth(id, agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, cardno, logkv, trxcode, trxdir, state, amount, settleamount,currency, trxtime, mcc, trxaddr, authcode, create_date)\n" +
            "values (#{id}, #{agentId}, #{agentName}, #{merchantId}, #{merchantName}, #{subId}, #{subName}, #{cardno}, #{logkv}, #{trxcode}, #{trxdir}, #{state}, #{amount}, #{settleamount}, #{currency}, #{trxtime}, #{mcc}, #{trxaddr}, #{authcode}, #{createDate} )\n" +
            "on duplicate key update\n" +
            "logkv = #{logkv},\n" +
            "trxcode = #{trxcode},\n" +
            "trxdir = #{trxdir},\n" +
            "state = #{state},\n" +
            "amount = #{amount},\n" +
            "settleamount = #{settleamount},\n" +
            "currency = #{currency},\n" +
            "trxtime = #{trxtime},\n" +
            "mcc = #{mcc},\n" +
            "trxaddr = #{trxaddr},\n" +
            "authcode = #{authcode}\n")
    void saveOrUpdate(JAuthEntity entity);
}