-- j_authed:  全部是子商户交易
drop table if exists j_authed;
create table j_authed
(
    id            bigint         not null,
    -- ids
    agent_id      bigint,
    agent_name    varchar(50),
    merchant_id   bigint         not null comment '商户ID',
    merchant_name varchar(50)    not null comment '商户名称',
    sub_id        bigint,
    sub_name      varchar(50),

    wallet_id     bigint comment '钱包id',
    wallet_name   varchar(50) comment '钱包用户',


    -- info
    cardno        varchar(16)    not null, -- 卡号	cardno	String	16	Y
    trxtype       varchar(25)    not null, -- 交易类型	trxtype	String	25	Y	见附录【授权交易类型】
    trxdir        varchar(6),              -- 交易方向	trxdir	String	6	O	付款：101014 收款：101013
    state         varchar(10),             -- 交易状态	state	String	10	O	见附录【授权交易状态】
    amount        decimal(18, 6) not null, -- 交易金额	amount	Number	18,6	Y
    currency      varchar(3)     not null, -- 交易币种	currency	String	3	Y
    entryamount   decimal(18, 6) not null, -- 入账金额	entryamount	Number	18,6	Y
    entrycurrency varchar(3)     not null, -- 入账币种	entrycurrency	String	3	Y
    trxtime       varchar(20)    not null, -- 交易时间	trxtime	string	20	Y	YYYY-MM-DD HH:mm:ss
    entrydate     varchar(8),              -- 入账日期	entrydate	String	8	O	YYYYMMDD
    chnltrxseq    varchar(50),             -- 入账流水号	chnltrxseq	String	50	O
    trxaddr       varchar(160),            -- 交易地点	trxaddr	String	160	O
    authcode      varchar(50),             -- 授权码	authcode	String	50	O
    logkv         varchar(50),             -- 流水号	logkv	String	50	O
    mcc           varchar(20),             -- 商户类别代码	mcc	String	20	O
    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_authed';
create index idx_j_authed_1 on j_authed (agent_id, create_date);
create index idx_j_authed_2 on j_authed (merchant_id, create_date);
create index idx_j_authed_3 on j_authed (sub_id, create_date);
create index idx_j_authed_4 on j_authed (sub_id, cardno, create_date);
create index idx_j_authed_5 on j_authed (cardno, create_date);
