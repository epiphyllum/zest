-- 钱包交易: 充值 + 提现
drop table if exists j_wallet_txn;
create table j_wallet_txn
(
    id            bigint         not null comment 'ID',
    agent_id      bigint         not null comment '代理id',
    agent_name    varchar(50)    not null comment '代理',
    merchant_id   bigint         not null comment '商户ID', -- 商户ID
    merchant_name varchar(50)    not null comment '商户',   -- 商户
    sub_id        bigint         not null comment '子商户ID',
    sub_name      varchar(50)    not null comment '子商户',
    api           int            not null default 1,

    --
    wallet_id     bigint         not null comment '钱包ID',
    wallet_name   varchar(64)    not null comment '钱包用户',

    channel_id    bigint         not null comment '收款渠道',
    channel_name  varchar(16)    not null comment 'USDT渠道, OneWay渠道',

    currency      varchar(3)     not null comment '本币币种:HKD|USD, 如果是USD就上账到USD, 如果是HKD就上账到HKD',
    stl_amount    decimal(18, 2) not null comment '到账本币金额',

    -- 依据到账金额 + 到账币种，
    -- 计算出客户需要发起的收单交易金额， 或者如果是U的话， 计算出需要收U的钱

    -- 交易信息
    txn_code      varchar(16)    not null comment 'charge:充值|withdraw:提现',
    pay_amount    decimal(18, 2) not null comment '交易金额${currency}',
    pay_currency  varchar(4)     not null comment '收款币种',
    pay_cost      decimal(18, 2) not null default 0 comment '渠道成本',

    -- 状态
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
