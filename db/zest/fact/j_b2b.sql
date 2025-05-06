-- b2b入金
drop table if exists j_b2b;
create table j_b2b
(
    id                  bigint         not null comment 'ID',
    agent_id            bigint         not null comment '代理ID',
    agent_name          varchar(50)    not null comment '代理名称',
    merchant_id         bigint         not null comment '商户ID',
    merchant_name       varchar(50)    not null comment '商户名',

    state               varchar(2)     not null default '00',
    error               varchar(128)   null,

    nid                 varchar(32)    not null comment '通知ID',
    bid                 varchar(18)    not null comment '业务关联ID',
    acctno              varchar(15)    not null comment '通联内部虚拟号',
    currency            varchar(15)    not null comment '币种',
    trxcod              varchar(10)    not null comment '交易代码',
    amount              decimal(18, 2) not null comment '金额',
    time                varchar(25)    not null comment '入账时间',
    payeraccountname    varchar(120) comment '户名',
    payeraccountno      varchar(45) comment '账号',
    payeraccountbank    varchar(30) comment '银行',
    payeraccountcountry varchar(3) comment '国家',
    ps                  varchar(200) comment '附言',

    eco_applyid         varchar(32) comment '生态圈转账applyid',
    fun_applyid         varchar(32) comment '同名转账applyid',
    eco_meraplid        varchar(32) comment '生态圈转账applyid',
    fun_meraplid        varchar(32) comment '同名转账applyid',

    creator             bigint comment '创建者',
    create_date         datetime comment '创建时间',
    updater             bigint comment '更新者',
    update_date         datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_b2b';

-- 普通索引
create index idx_j_b2b_1 on j_b2b (agent_id, create_date);
create index idx_j_b2b_2 on j_b2b (merchant_id, create_date);

-- 唯一索引
create unique index uidx_j_b2b_1 on j_b2b (merchant_id, eco_applyid);
create unique index uidx_j_b2b_2 on j_b2b (fun_applyid);
