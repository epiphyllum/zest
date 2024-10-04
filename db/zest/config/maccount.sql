-- 商户来账绑定账户
drop table if exists j_maccount;
create table j_maccount
(
    id             bigint      not null comment 'ID',

    -- 4
    agent_id       bigint      not null comment '代理ID', --
    agent_name     varchar(50) not null comment '代理',   --
    merchant_id    bigint      not null comment '商户ID', --
    merchant_name  varchar(50) not null comment '商户名', --

    -- mode
    api            int         not null default 1,

    -- 17
    flag           varchar(1),                            -- 账户类型  0:个人，1：企业
    currency       varchar(32),                           -- 币种: 多币种逗号（,）隔开
    country        varchar(3),                            -- 注册国家/地区 国别字典
    idtype         varchar(2),                            -- 证件类型
    idno           varchar(30),                           -- 证件号码 账户类型为个人时必填
    cardno         varchar(30),                           -- 账户号码
    cardname       varchar(16),                           -- 账户名称
    tel            varchar(20),                           -- 联系人电话
    email          varchar(50),                           -- 联系人邮箱
    accountaddr    varchar(100),                          -- 联系人详细地址
    bankname       varchar(16),                           -- 银行名称
    bankaddr       varchar(200),                          -- 开户行详情地址
    interbankmsg   varchar(100),                          -- 中转行swiftCode
    swiftcode      varchar(20),                           -- swiftcodeString20C银行所在国家/地区除了HKG以外，其他都必填
    depositcountry varchar(3),                            -- 银行所在国家: 国别字典
    biccode        varchar(30),                           --
    branchcode     varchar(30),                           --

    state          varchar(2),
    stateexplain   varchar(100),
    card_id        varchar(15),


    -- basic
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER
      SET utf8mb4 COMMENT = 'j_maccount';
create index idx_j_maccount_1 on j_maccount (merchant_id, create_date);
create index idx_j_maccount_2 on j_maccount (agent_id, create_date);
