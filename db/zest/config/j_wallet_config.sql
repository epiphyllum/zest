-- 钱包运营配置
drop table if exists j_wallet_config;
create table j_wallet_config
(
    id                bigint         not null comment 'ID',
    agent_id          bigint         not null comment '代理id',
    agent_name        varchar(50)    not null comment '代理',
    merchant_id       bigint         not null comment '商户ID', -- 商户ID
    merchant_name     varchar(50)    not null comment '商户',   -- 商户
    sub_id            bigint         not null comment '子商户ID',
    sub_name          varchar(50)    not null comment '子商户',

    -- 最小充值金额
    min_hkd           decimal(18, 2) not null comment '最小充值港币',
    min_usd           decimal(18, 2) not null comment '最小充值美金',

    -- 子商户va报警金额
    min_va_hkd        decimal(18, 2) not null default 10000 comment '最小va港币账户',
    min_va_usd        decimal(18, 2) not null default 10000 comment '最小va',

    -- 收费
    charge_rate       decimal(18, 3) not null comment '用户充值手续费率',
    withdraw_rate     decimal(18, 3) not null comment '提现手续费',
    usdt_withdraw_fee decimal(18, 2) not null comment '提U手续费',
    verify_fee        decimal(18, 2) not null comment '实名认证手续费',
    usdt_swap_rate    decimal(18, 3) not null comment 'usdt兑换手续费',

    -- 开卡收费:
    vpa_open_fee      decimal(18, 2) not null comment '匿名卡开卡费',
    vcc_open_fee      decimal(18, 2) not null comment '实名卡开卡费',
    real_open_fee     decimal(18, 2) not null comment '实体卡开卡费',

    -- 月费
    vcc_month_fee     decimal(18, 2) not null comment '虚拟卡月费',
    real_month_fee    decimal(18, 2) not null comment '实体卡月费',
    vpa_month_fee     decimal(18, 2) not null comment '匿名卡月费',

    -- 账户升级费
    upgrade_fee       decimal(18, 2) not null comment '升级账户费用',

    -- 港币汇率
    hkd_rate          decimal(18, 4) not null comment '港币汇率',

    -- 邮箱配置, 域名, telegram
    mail_host         varchar(32)    not null comment '',
    mail_port         varchar(6)     not null comment '',
    mail_user         varchar(32)    not null comment '',
    mail_pass         varchar(64)    not null comment '',
    mail_from         varchar(32)    not null comment '',
    domain            varchar(128)   not null comment '对外域名',
    protocol          varchar(16)    not null default 'https',
    telegram_key      varchar(128)   null comment '运营telegram key',
    telegram_group    varchar(64)    not null comment '社区',
    telegram_help     varchar(64)    not null comment '客服',

    -- 推广费率
    tron_url          varchar(128)   not null comment '',
    s1_open_rate      decimal(18, 2) not null comment '一级开卡分佣',
    s2_open_rate      decimal(18, 2) not null comment '二级开卡分拥',
    s1_charge_rate    decimal(18, 2) not null comment '一级充值分佣',
    s2_charge_rate    decimal(18, 2) not null comment '二级充值分拥',

    -- basic
    creator           bigint comment '创建者',
    create_date       datetime comment '创建时间',
    updater           bigint comment '更新者',
    update_date       datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_wallet_config';
create index idx_j_wallet_config_1 on j_wallet_config (agent_id, merchant_id, sub_id, create_date);
create index idx_j_wallet_config_2 on j_wallet_config (merchant_id, sub_id, create_date);
create index idx_j_wallet_config_3 on j_wallet_config (sub_id, create_date);

-- 唯一索引
create unique index uidx_j_wallet_config_1 on j_wallet_config (sub_id);
