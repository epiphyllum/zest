-- j_free: 全部是子商户的: 担保金释放
drop table if exists j_free;
create table j_free
(
    id            bigint         not null,

    cardno        varchar(16)  not null comment '卡号',     -- 卡号	cardno	String	16	Y
    logkv         varchar(50)  comment '流水号',               -- 流水号	logkv	String	50	O
    trxcode       varchar(25) comment '交易代码',
    trxdir        varchar(6)   comment '交易方向',              -- 交易方向	trxdir	String	6	O	付款：101014 收款：101013
    state         varchar(10) comment '交易状态',             -- 交易状态	state	String	10	O	见附录【授权交易状态】
    amount        decimal(18, 6) comment '交易金额', -- 交易金额	amount	Number	18,6	Y
    currency      varchar(3)     comment '币种',     -- 交易币种	currency	String	3	Y
    trxtime       varchar(20)    comment '交易时间', -- 交易时间	trxtime	string	20	Y	YYYY-MM-DD HH:mm:ss
    mcc           varchar(20) comment '商户类别代码',         -- 商户类别代码	mcc	String	20	O
    trxaddr       varchar(160) comment '交易地点',            -- 交易地点	trxaddr	String	160	O
    freecode      varchar(50) comment '授权码',               -- 授权码	freecode	String	50	O
    time          varchar(25),
    stateexplain  varchar(200),

    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_free';
create index idx_j_free_1 on j_free (agent_id, create_date);
create index idx_j_free_2 on j_free (merchant_id, create_date);
create index idx_j_free_3 on j_free (sub_id, create_date);
create index idx_j_free_4 on j_free (cardno, create_date);
create unique index uidx_jfree_1 on j_free(cardno, logkv);
