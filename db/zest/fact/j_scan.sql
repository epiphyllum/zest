-- tron流水
drop table if exists j_tron;
create table j_tron
(
    id            bigint         not null comment 'ID',
    agent_id      bigint         not null comment '代理ID',
    agent_name    varchar(50)    not null comment '代理名称',
    merchant_id   bigint         not null comment '商户ID',
    merchant_name varchar(50)    not null comment '商户名称',
    sub_id        bigint         not null comment '子商户ID',
    sub_name      varchar(50)    not null comment '子商户名称',
    wallet_id     bigint         not null comment '钱包ID',
    currency      varchar(4)     not null comment 'USDT',
    network       varchar(16)    not null comment 'trc20, poly',

    from_address  varchar(128)   not null,
    to_address    varchar(128)   not null,
    amount        decimal(18, 2) not null comment '金额',
    ts            bigint         not null,
    flag          varchar(1)     not null,
    txid          varchar(128)   not null,

    -- 上账状态
    state         varchar(2)     not null default '00' comment '状态',

    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_tron';
create index idx_j_tron_1 on j_tron (merchant_id, create_date);
create index idx_j_tron_2 on j_tron (sub_id, create_date);
create index idx_j_tron_3 on j_tron (agent_id, create_date);
