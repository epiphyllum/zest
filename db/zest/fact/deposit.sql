

-- 子商户给卡充值
drop table if exists j_deposit;
create table j_deposit
(
    id             bigint         not null,

    agent_id    bigint      not null comment '代理ID',
    agent_name  varchar(32) not null comment '代理名',
    merchant_id    bigint      not null comment '商户ID',
    merchant_name  varchar(32) not null comment '商户名',
    sub_id         bigint         not null comment '子商户ID',
    sub_name       varchar(32)    not null comment '子商户',

    meraplid       varchar(32)    not null,                    -- 申请单流水	meraplid	String	32	Y
    cardno         varchar(30)    not null,                    -- 卡号	cardno	String	30	Y
    payerid        varchar(30)    not null,                    -- 出账账户	payerid	String	30	Y
    currency       varchar(3)     not null,                    -- 币种
    amount         decimal(18, 2) not null,                    -- 缴纳金额	amount	Number	18,2	Y

    payeeaccount   varchar(30) comment '交易对手',             -- 交易对手	payeeaccount	String	30	O
    procurecontent varchar(200) comment '采购内容',            -- 采购内容	procurecontent	String	200	O
    agmfid         varchar(100) comment '合同文件',            -- 保证金对应合同协议	agmfid	String	100	O
    applyid        varchar(32)    not null comment '申请单号', -- 申请单号	applyid	String	32	Y
    -- extra
    `status`       int            not null default 0 comment '状态',

    -- basic
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_deposit';
create index idx_j_deposit on j_deposit (dept_id, create_date);
