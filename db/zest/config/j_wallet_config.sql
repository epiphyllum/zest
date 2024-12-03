-- 钱包运营配置
drop table if exists j_wallet_config;
create table j_wallet_config
(
    id                     bigint         not null comment 'ID',
    agent_id               bigint         not null comment '代理id',
    agent_name             varchar(50)    not null comment '代理',
    merchant_id            bigint         not null comment '商户ID', -- 商户ID
    merchant_name          varchar(50)    not null comment '商户',   -- 商户
    sub_id                 bigint         not null comment '子商户ID',
    sub_name               varchar(50)    not null comment '子商户',

    charge_rate            decimal(18, 3) not null comment '用户充值手续费率',
    min_hkd                decimal(18, 2) not null comment '最小充值港币',
    min_usd                decimal(18, 2) not null comment '最小充值美金',
    min_usdt               decimal(18, 2) not null comment '最小充值u',

    -- 邮箱配置
    mail_host              varchar(64),
    mail_username          varchar(64),
    mail_password          varchar(64),
    mail_protocol          varchar(16),
    mail_sendmail          varchar(64),
    mail_sendname          varchar(64),
    mail_auth              tinyint,
    mail_starttls          tinyint,
    mail_starttls_required tinyint,
    mail_ssl               tinyint,

    -- 域名
    domain                 varchar(128)   not null comment '对外域名',

    -- basic
    creator                bigint comment '创建者',
    create_date            datetime comment '创建时间',
    updater                bigint comment '更新者',
    update_date            datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_wallet_config';
create index idx_j_wallet_config_1 on j_wallet_config (agent_id, merchant_id, sub_id, create_date);
create index idx_j_wallet_config_2 on j_wallet_config (merchant_id, sub_id, create_date);
create index idx_j_wallet_config_3 on j_wallet_config (sub_id, create_date);

-- 唯一索引
create unique index uidx_j_wallet_config_1 on j_wallet_config (sub_id);
