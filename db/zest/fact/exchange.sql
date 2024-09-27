-- j_exchange
drop table if exists j_exchange;
create table j_exchange
(
    id             bigint      not null comment 'ID',

    agent_id       bigint      not null comment '代理ID',
    agent_name     varchar(32) not null comment '代理名',
    merchant_id    bigint      not null comment '商户ID',
    merchant_name  varchar(32) not null comment '商户名',

    meraplid       varchar(32) not null comment '申请单流水',
    payeeccy       varchar(3) comment '到账币种',
    payerccy       varchar(3) comment '卖出币种',
    lockamountflag varchar(1)  not null default 'S' comment '锁定方',
    amount         decimal(18, 2) comment '金额',

    -- response
    applyid        varchar(32) comment '申请单号',

    -- lock
    feecurrency    varchar(16) comment '手续费币种',
    settleamount   decimal(18, 6) comment '结算金额',
    settlecurrency varchar(3) comment '结算币种',
    fxrate         decimal(18, 6) comment '汇率',
    fee            decimal(18, 6) comment '手续费',

    -- execute
    extype         varchar(2) comment '锁汇方式', --  LK:锁汇成交 MT：现价锁汇成交

    -- 执行情况
    stlamount      decimal(18, 2),                -- 执行最终的结算金额
    exfee          decimal(18, 2),                -- 执行手续费
    exfxrate       decimal(18, 6),                -- 执行汇率

    -- basic
    state          varchar(16) comment '状态',

    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_exchange';
create index idx_j_exchange on j_exchange (dept_id, create_date);

