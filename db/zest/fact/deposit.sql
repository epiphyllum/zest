-- 子商户给卡充值
drop table if exists j_deposit;
create table j_deposit
(
    id             bigint         not null,

    -- 6
    agent_id       bigint         not null comment '代理ID',
    agent_name     varchar(32)    not null comment '代理名',
    merchant_id    bigint         not null comment '商户ID',
    merchant_name  varchar(32)    not null comment '商户名',
    sub_id         bigint         not null comment '子商户ID',
    sub_name       varchar(32)    not null comment '子商户',
    api            int            not null default 1,

    -- 请求字段(7)
    meraplid       varchar(32)    not null,                -- 申请单流水	meraplid	String	32	Y
    cardno         varchar(30)    not null,                -- 卡号	cardno	String	30	Y
    payerid        varchar(30)    not null,                -- 出账账户	payerid	String	30	Y
    amount         decimal(18, 2) not null,                -- 缴纳金额	amount	Number	18,2	Y
    payeeaccount   varchar(30) comment '交易对手',         -- 交易对手	payeeaccount	String	30	O
    procurecontent varchar(200) comment '采购内容',        -- 采购内容	procurecontent	String	200	O
    agmfid         varchar(100) comment '合同文件',        -- 保证金对应合同协议	agmfid	String	100	O

    -- 冗余增加的(1)
    currency       varchar(3)     not null,                -- 币种

    -- 发起金额
    txn_amount     decimal(18, 2) not null,

    -- 通联返回(2)
    applyid        varchar(32)    null comment '申请单号', -- 申请单号	applyid	String	32	Y
    `state`        varchar(2)     not null default 0 comment '状态',
    `stateexplain` varchar(200)   null,
    securityamount decimal(18, 2) null,
    fee            decimal(18,2)  null,

    -- basic(4)
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_deposit';
create index idx_j_deposit_1 on j_deposit (agent_id, create_date);
create index idx_j_deposit_2 on j_deposit (merchant_id, create_date);
create index idx_j_deposit_3 on j_deposit (sub_id, create_date);
create index idx_j_deposit_4 on j_deposit (applyid);
create index idx_j_deposit_4 on j_deposit (meraplid);
