-- 提现数据分类汇总
drop view if exists v_withdraw;
create view v_withdraw as
select sum(amount)       as card_sum,
       sum(fee)          as aip_charge,
       sum(merchantfee)  as charge,

       agent_id,
       agent_name,
       merchant_id,
       merchant_name,
       sub_id,
       sub_name,
       currency,
       marketproduct,
       stat_date
from j_withdraw
where state = '04'
group by stat_date, agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, currency, marketproduct;
