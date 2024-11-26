-- sum(trxcode =='Auth' and state == '成功' and  trxdir == '付款’）
--  - sum(trxcode == 'ReturnOL' and state == '成功' and trxdir == '收款')
--  - sum(trxcode =='Auth' and state == '成功' &&  trxdir == '收款')

-- 付款：101014
-- 收款：101013

--
-- 发卡数量分类汇总
drop view if exists v_auth;
create view v_auth as

select sum(if(trxcode = 'Auth' && trxdir = '101014', settleamount, 0)) -
       sum(if(trxcode = 'ReturnOL' && trxdir = '101013', settleamount, 0)) -
       sum(if(trxcode = 'Auth' && trxdir = '101013', settleamount, 0)) as settleamount,
       sum(if(trxcode = 'Auth' && trxdir = '101014', 1, 0)) -
       sum(if(trxcode = 'ReturnOL' && trxdir = '101013', 1, 0)) -
       sum(if(trxcode = 'Auth' && trxdir = '101013', 1, 0))            as settlecount,
       currency,
       marketproduct,
       merchant_id,
       merchant_name,
       sub_id,
       sub_name,
       agent_id
from j_auth

where state = '00'
group by agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, currency, marketproduct;


select *
from v_auth;
