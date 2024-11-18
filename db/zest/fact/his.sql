drop table if exists j_deposit_stat;
create table j_deposit_stat
(
    id            bigint not null,
    -- 维度信息
    agent_id      bigint comment '代理ID',
    merchant_id   bigint comment '商户id',
    sub_id        bigint comment '子商户id',
    stat_date     date comment '统计日期',
    currency      varchar(3) comment '币种',
    marketproduct varchar(16) comment '产品类型',

    -- 发卡数据统计量
    card_fee      decimal(18, 2) comment '开卡费用',
    aip_card_fee  decimal(18, 2) comment '开卡费用-成本',
    card_sum      decimal(18, 2) comment '充值总额',
    aip_card_sum  decimal(18, 2) comment '发起充值总额',
    charge        decimal(18, 2) comment '充值手续费',
    aip_charge    decimal(18, 2) comment '充值手续费-成本',
    deposit       decimal(18, 2) comment '保证金',
    aip_deposit   decimal(18, 2) comment '保证金-成本',
    total_card    bigint comment '发卡量',

    -- basic(4)
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) engine = innodb
  collate utf8mb4_bin
  default character set utf8mb4 comment ='j_deposit_stat';

-- 代理视图
create index idx_j_deposit_state_1 on j_deposit_stat(agent_id, currency, marketproduct, stat_date);
create index idx_j_deposit_state_2 on j_deposit_stat(agent_id, currency, stat_date);

-- 商户视图
create index idx_j_deposit_state_3 on j_deposit_stat(merchant_id, currency, marketproduct, stat_date);
create index idx_j_deposit_state_4 on j_deposit_stat(merchant_id, currency, stat_date);

-- 子商户视图
create index idx_j_deposit_state_5 on j_deposit_stat(sub_id, currency, marketproduct, stat_date);
create index idx_j_deposit_state_6 on j_deposit_stat(sub_id, currency, stat_date);

-- 机构视图
create index idx_j_deposit_state_7 on j_deposit_stat(currency, marketproduct, stat_date);
create index idx_j_deposit_state_8 on j_deposit_stat(currency, stat_date);

