-- 1. j_card
-- 2. j_deposit
-- 3. j_withdraw

-- 子商户每日统计
select count(1)
from j_card;

-- 发卡数量分类汇总
select count(1)          as total_card,
       date(create_date) as stat_date,
       currency,
       merchant_id,
       sub_id,
       agent_id
from j_card
where create_date >= '2024-10-10'
  and create_date <= '2024-12-01'
  and state = '03'
group by stat_date, agent_id, merchant_id, sub_id, currency;

-- 充值数据分类汇总
select sum(merchant_charge)  as charge,
       sum(merchant_deposit) as deposit,
       sum(fee)              as aip_charge,
       sum(securityamount)   as aip_deposit,
       sum(amount)           as card_sum,
       sum(txn_amount)       as aip_card_sum,
       agent_id,
       merchant_id,
       sub_id,
       currency,
       date(create_date)     as stat_date
from j_deposit
where state = '04'
  and create_date >= '2024-10-10'
  and create_date <= '2024-12-01'
group by stat_date, agent_id, merchant_id, sub_id, currency;

-- 提现数据分类汇总
select sum(amount)       as aip_card_sum,
       sum(fee)          as aip_charge,
       agent_id,
       merchant_id,
       sub_id,
       currency,
       date(create_date) as stat_date
from j_withdraw
where state = '04'
  and create_date >= '2024-10-10'
  and create_date <= '2024-12-01'
group by stat_date, agent_id, merchant_id, sub_id, currency;
