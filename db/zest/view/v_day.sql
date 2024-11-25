-- 机构+币种/
select sum(card_sum)            as card_sum,
       sum(charge)              as charge,
       sum(deposit)             as deposit,
       sum(aip_card_sum)        as aip_card_sum,
       sum(aip_charge)          as aip_charge,
       sum(aip_deposit)         as aip_deposit,
       sum(withdraw)            as withdraw,
       sum(withdraw_charge)     as withdraw_charge,
       sum(aip_withdraw_charge) as aip_withdraw_charge,
       sum(card_fee)            as card_fee,
       sum(aip_card_fee)        as aip_card_fee,
       currency
from j_stat
group by currency;

-- 机构+币种 + 产品
select sum(card_sum)            as card_sum,
       sum(charge)              as charge,
       sum(deposit)             as deposit,
       sum(aip_card_sum)        as aip_card_sum,
       sum(aip_charge)          as aip_charge,
       sum(aip_deposit)         as aip_deposit,
       sum(withdraw)            as withdraw,
       sum(withdraw_charge)     as withdraw_charge,
       sum(aip_withdraw_charge) as aip_withdraw_charge,
       sum(card_fee)            as card_fee,
       sum(aip_card_fee)        as aip_card_fee,
       currency,
       marketproduct
from j_stat
group by currency;

-- 按币种 + 产品
select sum(card_sum)            as card_sum,
       sum(charge)              as charge,
       sum(deposit)             as deposit,
       sum(aip_card_sum)        as aip_card_sum,
       sum(aip_charge)          as aip_charge,
       sum(aip_deposit)         as aip_deposit,
       sum(withdraw)            as withdraw,
       sum(withdraw_charge)     as withdraw_charge,
       sum(aip_withdraw_charge) as aip_withdraw_charge,
       sum(card_fee)            as card_fee,
       sum(aip_card_fee)        as aip_card_fee,
       currency,
       marketproduct
from j_stat
group by currency, marketproduct;

-- 按币种 + 产品 - 子商户
select sum(card_sum)            as card_sum,
       sum(charge)              as charge,
       sum(deposit)             as deposit,
       sum(aip_card_sum)        as aip_card_sum,
       sum(aip_charge)          as aip_charge,
       sum(aip_deposit)         as aip_deposit,
       sum(withdraw)            as withdraw,
       sum(withdraw_charge)     as withdraw_charge,
       sum(aip_withdraw_charge) as aip_withdraw_charge,
       sum(card_fee)            as card_fee,
       sum(aip_card_fee)        as aip_card_fee,
       currency,
       marketproduct,
       sub_id
from j_stat
group by currency, marketproduct, sub_id;

-- 按币种 + 产品 - 商户
select sum(card_sum)            as card_sum,
       sum(charge)              as charge,
       sum(deposit)             as deposit,
       sum(aip_card_sum)        as aip_card_sum,
       sum(aip_charge)          as aip_charge,
       sum(aip_deposit)         as aip_deposit,
       sum(withdraw)            as withdraw,
       sum(withdraw_charge)     as withdraw_charge,
       sum(aip_withdraw_charge) as aip_withdraw_charge,
       sum(card_fee)            as card_fee,
       sum(aip_card_fee)        as aip_card_fee,
       currency,
       marketproduct,
       merchant_id
from j_stat
group by currency, marketproduct, merchant_id
