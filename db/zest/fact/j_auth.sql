-- 授权交易
drop table if exists j_auth;
create table j_auth
(
    id             bigint      not null,

    agent_id       bigint      not null,
    agent_name     varchar(50) not null,
    merchant_id    bigint      not null comment '子商户ID',
    merchant_name  varchar(32) not null comment '子商户',
    sub_id         bigint      not null,
    sub_name       varchar(50) not null,
    marketproduct  varchar(16) not null comment '卡产品',

    -- todo: 钱包
    wallet_id      bigint comment '钱包id',
    --
    maincardno    varchar(50) comment '主卡',

    -- cardapplyid    varchar(30),
    cardno         varchar(16) not null comment '卡号',
    logkv          varchar(50) comment '流水号',
    trxcode        varchar(25) comment '交易代码',
    trxdir         varchar(6) comment '交易方向',
    state          varchar(10) comment '交易状态',
    respmsg        varchar(256) comment '应答消息',
    amount         decimal(18, 2) comment '交易金额',
    currency       varchar(3) comment '币种',
    settleamount   decimal(18, 2),
    settlecurrency varchar(3),
    trxtime        varchar(20) comment '交易时间',
    mcc            varchar(20) comment '商户类别代码',
    trxaddr        varchar(160) comment '交易地点',
    authcode       varchar(50) comment '授权码',
    time           varchar(25),
    stateexplain   varchar(200),

    -- basic
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_auth';
create index idx_j_auth_1 on j_auth (agent_id, create_date);
create index idx_j_auth_2 on j_auth (merchant_id, create_date);
create index idx_j_auth_3 on j_auth (sub_id, create_date);
create index idx_j_auth_4 on j_auth (cardno, create_date);
create unique index uidx_j_auth_1 on j_auth (cardno, logkv);
