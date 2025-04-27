-- 卡片充值
drop table if exists j_deposit;
create table j_deposit
(
    id                bigint         not null,

    -- 6
    agent_id          bigint         not null comment '代理ID',
    agent_name        varchar(32)    not null comment '代理名',
    merchant_id       bigint         not null comment '商户ID',
    merchant_name     varchar(32)    not null comment '商户名',
    sub_id            bigint         not null comment '子商户ID',
    sub_name          varchar(32)    not null comment '子商户',
    api               int            not null default 1,

    -- 请求字段(7)
    meraplid          varchar(32)    not null,
    marketproduct     varchar(16)    not null,
    wallet_id         bigint         null comment '钱包ID',
    cardno            varchar(30)    not null,
    payerid           varchar(30)    not null,
    amount            decimal(18, 2) not null comment '要求到账金额',
    payeeaccount      varchar(30) comment '交易对手',
    procurecontent    varchar(200) comment '采购内容',
    agmfid            varchar(100) comment '合同文件',

    -- 冗余增加的(1)
    currency          varchar(3)     not null,                -- 币种
    txn_amount        decimal(18, 2) not null,                -- 发起金额
    merchant_charge   decimal(18, 2) not null,                -- 发起金额
    merchant_deposit  decimal(18, 2) not null,                -- 发起金额

    charge_rate       decimal(18, 2) not null,                -- 发起金额
    deposit_rate      decimal(18, 2) not null,                -- 发起金额
    cost_charge_rate  decimal(18, 2) not null,                -- 发起金额
    cost_deposit_rate decimal(18, 2) not null,                -- 发起金额

    -- 通联返回(2)
    applyid           varchar(32)    null comment '申请单号', -- 申请单号	applyid	String	32	Y
    `state`           varchar(2)     not null default 0 comment '状态',
    `stateexplain`    varchar(200)   null,

    feecurrency       varchar(3) comment '币种',
    fee               decimal(18, 2) comment '手续费',
    securityamount    decimal(18, 2) null,
    securitycurrency  varchar(3) comment '币种',

    -- basic(4)
    stat_date         date comment '完成日期',
    creator           bigint comment '创建者',
    create_date       datetime comment '创建时间',
    updater           bigint comment '更新者',
    update_date       datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_deposit';

-- 普通索引
create index idx_j_deposit_1 on j_deposit (agent_id, create_date);
create index idx_j_deposit_2 on j_deposit (merchant_id, create_date);
create index idx_j_deposit_3 on j_deposit (sub_id, create_date);
create index idx_j_deposit_4 on j_deposit (merchant_id, meraplid);

-- 唯一索引
create unique index uidx_j_deposit_1 on j_deposit (applyid);