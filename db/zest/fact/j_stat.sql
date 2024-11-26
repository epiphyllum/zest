drop table if exists j_stat;
create table j_stat
(
    id                  bigint         not null,
    md5                 varchar(32)    not null,

    -- 维度信息
    agent_id            bigint comment '代理ID',
    agent_name          varchar(50),
    merchant_id         bigint comment '商户id',
    merchant_name       varchar(50),
    sub_id              bigint comment '子商户id',
    sub_name            varchar(50),
    currency            varchar(3) comment '币种',
    marketproduct       varchar(16) comment '产品类型',

    -- 日期
    stat_date           date comment '统计日期',

    -- 充值数据
    card_sum            decimal(18, 2) comment '充值总额',
    charge              decimal(18, 2) comment '充值手续费',
    deposit             decimal(18, 2) comment '保证金',
    aip_card_sum        decimal(18, 2) comment '通联发起金额',
    aip_charge          decimal(18, 2) comment '充值手续费-成本',
    aip_deposit         decimal(18, 2) comment '保证金-成本',

    -- 提现数据
    withdraw            decimal(18, 2) comment '提现',
    withdraw_charge     decimal(18, 2) comment '',
    aip_withdraw_charge decimal(18, 2) comment '',

    -- 发卡数据-成本
    total_card          bigint comment '发卡量',
    card_fee            decimal(18, 2) comment '开卡费用',
    aip_card_fee        decimal(18, 2) comment '开卡成本',

    -- 结算数据
    settleamount        decimal(18, 2) not null default 0,
    settlecount         bigint         not null default 0,

    primary key (id)
) engine = innodb
  collate utf8mb4_bin
  default character set utf8mb4 comment ='j_stat';

-- 唯一索引
create unique index uidx_j_stat_1 on j_stat (md5);

-- 代理视图
create index idx_j_stat_1 on j_stat (agent_id, currency, marketproduct, stat_date);
create index idx_j_stat_2 on j_stat (agent_id, currency, stat_date);

-- 商户视图
create index idx_j_stat_3 on j_stat (merchant_id, currency, marketproduct, stat_date);
create index idx_j_stat_4 on j_stat (merchant_id, currency, stat_date);

-- 子商户视图
create index idx_j_stat_5 on j_stat (sub_id, currency, marketproduct, stat_date);
create index idx_j_stat_6 on j_stat (sub_id, currency, stat_date);

-- 机构视图
create index idx_j_stat_7 on j_stat (currency, marketproduct, stat_date);
create index idx_j_stat_8 on j_stat (currency, stat_date);
