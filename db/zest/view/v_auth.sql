-- 发卡数量分类汇总
drop view if exists v_auth;
create view v_auth as
select count(1)          as total_card,
       sum(fee) as fee,
       sum(merchantfee) as merchantfee,
       stat_date,
       currency,
       marketproduct,
       merchant_id,
       merchant_name,
       sub_id,
       sub_name,
       agent_id,
       agent_name
from j_card
where state = '04'
group by stat_date, agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, currency, marketproduct;
