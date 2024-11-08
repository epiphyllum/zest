-- withdraw
drop table if exists j_withdraw;
create table j_withdraw
(
    id             bigint         not null,

    agent_id       bigint         not null comment '代理ID',
    agent_name     varchar(32)    not null comment '代理名',
    merchant_id    bigint         not null comment '商户ID',
    merchant_name  varchar(32)    not null comment '商户名',
    sub_id         bigint         not null comment '子商户ID',
    sub_name       varchar(32)    not null comment '子商户',
    api            int            not null default 1,

    meraplid       varchar(32)    not null comment '申请单流水',
    marketproduct  varchar(17)    not null comment '产品',
    cardno         varchar(30)    not null comment '卡号',
    payeeid        varchar(30) comment '交易对手',
    currency       varchar(3) comment '币种',
    amount         decimal(18, 2) not null comment '缴纳金额',
    applyid        varchar(32)    null comment '申请单号',

    -- extra
    `state`        varchar(2)     not null default 0 comment '状态',
    stateexplain   varchar(200),
    securityamount decimal(18, 2) null,

    -- basic
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_withdraw';
create index idx_j_withdraw_1 on j_withdraw (agent_id, create_date);
create index idx_j_withdraw_2 on j_withdraw (merchant_id, create_date);
create index idx_j_withdraw_3 on j_withdraw (sub_id, create_date);
create index idx_j_withdraw_4 on j_withdraw (merchant_id, meraplid);
create unique index uidx_j_withdraw_1 on j_withdraw (applyid);


