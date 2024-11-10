-- 钱包交易
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

    maincardno    varchar(30)    not null comment '预付费主卡',
    amount        decimal(18, 2) not null comment '金额',
    txn_code      varchar(16)    not null comment 'charge | withdraw',
    meraplid      varchar(30)    not null comment '申请id',

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
create index idx_j_wallet_txn_4 on j_wallet_txn (sub_id, meraplid, create_date);
