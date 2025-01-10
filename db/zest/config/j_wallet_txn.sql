-- 钱包交易: 充值 + 提现
drop table if exists j_wallet_txn;
create table j_wallet_txn
(
    id            bigint      not null comment 'ID',
    agent_id      bigint      not null comment '代理id',
    agent_name    varchar(50) not null comment '代理',
    merchant_id   bigint      not null comment '商户ID', -- 商户ID
    merchant_name varchar(50) not null comment '商户',   -- 商户
    sub_id        bigint      not null comment '子商户ID',
    sub_name      varchar(50) not null comment '子商户',
    api           int         not null default 1,

    --
    wallet_id     bigint      not null comment '钱包ID',
    wallet_name   varchar(64) not null comment '钱包用户',

    txn_code      varchar(16) comment '交易代码: charge, withdraw, chargeCard, withdrawCard, monthFee...',
    txn_memo      varchar(128) comment '交易备注',
    fee           decimal(18, 2) comment '手续费: 从出金账户收',
    from_currency varchar(4) comment '出金资产: 数字货币:USDT, 法币:USD,HKD',
    from_amount   decimal(18, 2) comment '出金金额',
    to_currency   varchar(4) comment '入金账户: USDT, USDC, USD, HKD',
    to_amount     decimal(18, 2) comment '入金金额',

    -- 交易状态
    state         varchar(2),

    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_wallet_txn';
create index idx_j_wallet_txn_1 on j_wallet_txn (agent_id, merchant_id, sub_id, create_date);
create index idx_j_wallet_txn_2 on j_wallet_txn (merchant_id, sub_id, create_date);
create index idx_j_wallet_txn_3 on j_wallet_txn (sub_id, create_date);
