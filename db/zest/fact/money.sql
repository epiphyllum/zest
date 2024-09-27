--  入金账户来账流水: 商户匹配前, 不能确定merchant_id
drop table if exists j_money;
create table j_money
(
    id                  bigint not null comment 'ID',

    merchant_id         bigint comment '商户ID',
    merchant_name       varchar(50) comment '',

    -- 通知过来的内容
    nid                 varchar(32) comment '通知id',         -- 		String	32	Y	相同id表示同一个通知
    bid                 varchar(18) comment '业务关联id',     --	String	18	Y	业务关联id
    acctno              varchar(15) comment '账号',           -- 		String	15	Y	通联内部虚拟账号
    amount              decimal(18, 2) comment '变动金额',    -- 		Number	18,2	Y	变动金额
    currency            varchar(16) comment '币种',           -- 		String
    trxcod              varchar(10) comment '交易类型',       --		String	10	Y	CP201-汇款充值上账 CP213-伞形账户上账
    `time`              varchar(25) comment '入账时间',       --		String	25	Y	ISO格式[yyyy-MM-dd'T'HH:mm:ssZ]
    payeraccountname    varchar(120) comment '打款方姓名',    --		String	120	O	付款方账户名称
    payeraccountno      varchar(45) comment '打款方银行账号', --		String	45	O	付款方账户号
    payeraccountbank    varchar(30) comment '打款方银行号',   --		String	30	O
    payeraccountcountry varchar(3) comment '打款方国家',      --		String	3	O
    ps                  varchar(200) comment '附言',          --		String	200	O

    -- 匹配情况
    `status`            int    not null default 1 comment '1: 匹配成功, 0: 待匹配',
    notify_status       int    not null default 0 comment '',
    notify_count        int    not null default 0,

    creator             bigint comment '创建者',
    create_date         datetime comment '创建时间',
    updater             bigint comment '更新者',
    update_date         datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_money';
create index idx_j_money on j_money (merchant_id, create_date);
create unique index uidx_j_money_1 on j_money (nid);


-- 子商户给卡充值
drop table if exists j_deposit;
create table j_deposit
(
    id             bigint         not null,
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

-- withdraw
drop table if exists j_withdraw;
create table j_withdraw
(
    id          bigint         not null,
    sub_id      bigint         not null comment '子商户ID',
    sub_name    varchar(32)    not null comment '子商户',

    meraplid    varchar(32)    not null comment '申请单流水',
    cardno      varchar(30)    not null comment '卡号',
    payeeid     varchar(30) comment '交易对手',
    currency    varchar(3) comment '币种',
    amount      decimal(18, 2) not null comment '缴纳金额',
    applyid     varchar(32)    null comment '申请单号',

    -- extra
    `status`    int            not null default 0 comment 'status',

    -- basic
    version     int            not null default 0 comment '乐观锁版本号',
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_withdraw';
create index idx_j_withdraw on j_withdraw (dept_id, create_date);


