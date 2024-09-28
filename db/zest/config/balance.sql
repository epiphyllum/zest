-- j_balance
drop table if exists j_balance;
create table j_balance
(
    id           bigint         not null comment '余额ID',

    -- 余额基本信息: merchant_id / sub_id
    owner_id     bigint         not null comment '属主ID',
    owner_name   varchar(50)    not null comment '属主',
    owner_type   varchar(16)    not null comment '用户类型',

    balance_type varchar(16)    not null comment '余额类型',
    currency     varchar(3)     not null comment '币种',
    balance      decimal(18, 6) not null default 0 comment '余额',
    frozen       decimal(18, 6) not null default 0 comment '冻结额',
    version      int            not null default 0 comment '版本号',

    -- basic
    creator      bigint comment '创建者',
    create_date  datetime comment '创建时间',
    updater      bigint comment '更新者',
    update_date  datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_balance';
create index idx_j_balance on j_balance (owner_id, create_date);

-- j_log
drop table if exists j_log;
create table j_log
(
    id           bigint         not null,

    -- 余额基本信息
    owner_id     bigint         not null comment '属主ID',
    owner_name   varchar(50)    not null comment '属主',
    owner_type   varchar(16)    not null comment '用户类型',

    -- 余额基本信息
    balance_id   bigint         not null comment '余额ID',
    balance_type varchar(16)    not null comment '余额类型',
    balance_name varchar(32)    not null comment '余额名称',
    currency     varchar(3)     not null comment '币种',

    -- fact info
    fact_type    int            not null comment '凭证类型',
    fact_id      bigint         not null comment '凭证ID',
    fact_amount  decimal(18, 6) not null comment '凭证金额',
    fact_memo    varchar(100)   not null comment '凭证描述',
    old_balance  decimal(18, 6) not null comment '旧余额',
    new_balance  decimal(18, 6) not null comment '新余额',
    version      bigint         not null comment '新version',

    -- basic
    creator      bigint comment '创建者',
    create_date  datetime comment '创建时间',
    updater      bigint comment '更新者',
    update_date  datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_log';
create index idx_j_log_0 on j_log (owner_id, create_date);
create unique index uidx_j_log_0 on j_log (fact_type, fact_id);
