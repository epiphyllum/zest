-- 代理
drop table if exists j_agent;
create table j_agent
(
    id           bigint         not null comment 'ID', -- 代理ID

    -- 给到代理的充值费率
    agent_name   varchar(50)    not null,
    charge_rate  decimal(10, 3) not null comment '充值费率',

    -- 代理分佣收款账户 todo: other info
    account_no   varchar(128) comment '账号',
    account_user varchar(128) comment '账户名',
    account_bank varchar(128) comment '银行名',

    -- [a, b)
    first_limit  decimal(18, 2) not null comment '一档金额',
    first_rate   decimal(10, 3) not null comment '一档费率',
    second_limit decimal(18, 2) not null comment '二挡金额',
    second_rate  decimal(10, 3) not null comment '二挡费率',
    third_limit  decimal(18, 2) not null comment '三挡金额',
    third_rate   decimal(10, 3) not null comment '三挡费率',

    -- basic
    creator      bigint comment '创建者',
    create_date  datetime comment '创建时间',
    updater      bigint comment '更新者',
    update_date  datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_agent';
create index idx_j_agent_1 on j_agent (create_date);