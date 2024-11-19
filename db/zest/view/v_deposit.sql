-- 充值数据分类汇总
drop view if exists v_deposit;
create view v_deposit as
select sum(merchant_charge)  as charge,
       sum(merchant_deposit) as deposit,
       sum(fee)              as aip_charge,
       sum(securityamount)   as aip_deposit,
       sum(amount)           as card_sum,
       sum(txn_amount)       as aip_card_sum,

       agent_id,
       agent_name,
       merchant_id,
       merchant_name,
       sub_id,
       sub_name,
       currency,
       marketproduct,

       date(create_date)     as stat_date
from j_deposit
where state = '04'
group by stat_date, agent_id, agent_name, merchant_id, merchant_name, sub_id, sub_name, currency, marketproduct;
