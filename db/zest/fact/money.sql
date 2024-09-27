--  入金账户来账流水: 商户匹配前, 不能确定merchant_id
drop table if exists j_money;
create table j_money
(
    id                  bigint      not null comment 'ID',

    agent_id            bigint      not null comment '代理ID',
    agent_name          varchar(50) not null comment '代理名称',
    merchant_id         bigint      not null comment '商户ID',
    merchant_name       varchar(50) not null comment '商户名',

    -- 通知过来的内容
    nid                 varchar(32) comment '通知id',         -- 	相同id表示同一个通知
    bid                 varchar(18) comment '业务关联id',     --	业务关联id
    acctno              varchar(15) comment '账号',           -- 通联内部虚拟账号
    amount              decimal(18, 2) comment '变动金额',    --  变动金额
    currency            varchar(16) comment '币种',           --
    trxcod              varchar(10) comment '交易类型',       --	CP201-汇款充值上账 CP213-伞形账户上账
    `time`              varchar(25) comment '入账时间',       --	ISO格式[yyyy-MM-dd'T'HH:mm:ssZ]
    payeraccountname    varchar(120) comment '打款方姓名',    --	O	付款方账户名称
    payeraccountno      varchar(45) comment '打款方银行账号', --	O	付款方账户号
    payeraccountbank    varchar(30) comment '打款方银行号',   --
    payeraccountcountry varchar(3) comment '打款方国家',      --
    ps                  varchar(200) comment '附言',          --

    -- 匹配情况
    `status`            int         not null default 1 comment '1: 匹配成功, 0: 待匹配',
    notify_status       int         not null default 0 comment '',
    notify_count        int         not null default 0,

    creator             bigint comment '创建者',
    create_date         datetime comment '创建时间',
    updater             bigint comment '更新者',
    update_date         datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_money';
create index idx_j_money on j_money (merchant_id, create_date);
create index idx_j_money on j_money (agent_id, create_date);
create unique index uidx_j_money_1 on j_money (nid);
