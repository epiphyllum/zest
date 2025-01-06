-- 钱包管理
drop table if exists j_wallet;
create table j_wallet
(
    id                bigint         not null comment 'ID',
    agent_id          bigint         not null comment '代理id',
    agent_name        varchar(50)    not null comment '代理',
    merchant_id       bigint         not null comment '商户ID', -- 商户ID
    merchant_name     varchar(50)    not null comment '商户',   -- 商户
    sub_id            bigint         not null comment '子商户ID',
    sub_name          varchar(50)    not null comment '子商户',

    -- 账户等级: (独立主卡)
    hkd_level         varchar(16) comment '钱包等级: basic, premium',
    usd_level         varchar(16) comment '钱包等级: basic, premium',

    -- 账户升级后, 用户专属主卡
    hkd_cardno        varchar(30) comment '港币主卡',
    hkd_cardid        bigint,
    usd_cardno        varchar(30) comment '美金主卡',
    usd_cardid        bigint,

    -- 用户信息
    phone             varchar(50) comment '手机号',
    email             varchar(50) comment '邮箱',
    password          varchar(64) comment '密码',

    -- MFA
    totp_key          varchar(64) comment '秘钥',
    totp_status       int comment '绑定状态',

    -- 接入参数
    access_key        varchar(128) comment '接入密钥',

    -- usdt_trc20
    usdt_key          varchar(256) comment '私钥',
    usdt_trc20        varchar(64) comment 'currency + network',
    usdt_trc20_ts     bigint comment '最后一笔时间',
    usdt_trc20_fetch  datetime comment '最后一次更新时间',

    -- 推广
    refcode           varchar(8)     not null comment '推荐码',
    p1                bigint comment '直接上级ID',
    p2                bigint comment '间接上级ID',
    s1_count          bigint         not null default 0 comment '直接下级数',
    s2_count          bigint         not null default 0 comment '间接下级数',

    s1_open_fee_hkd   decimal(18, 2) not null default 0 comment '直接下级开卡贡献',
    s2_open_fee_hkd   decimal(18, 2) not null default 0 comment '间接下级开卡贡献',
    s1_charge_fee_hkd decimal(18, 2) not null default 0 comment '直接下级充值贡献',
    s2_charge_fee_hkd decimal(18, 2) not null default 0 comment '间接下级充值贡献',
    s1_open_fee_usd   decimal(18, 2) not null default 0 comment '直接下级开卡贡献',
    s2_open_fee_usd   decimal(18, 2) not null default 0 comment '间接下级开卡贡献',
    s1_charge_fee_usd decimal(18, 2) not null default 0 comment '直接下级充值贡献',
    s2_charge_fee_usd decimal(18, 2) not null default 0 comment '间接下级充值贡献',

    opened            int            not null default 0 comment '是否开卡',
    charged           int            not null default 0 comment '是否充值',
    version           bigint         not null default 0 comment '版本号',

    -- 实名信息
    first_name        varchar(64) comment '名字',
    last_name         varchar(64) comment '姓',
    country_code      varchar(6) comment '国家代码',
    id_no             varchar(32) comment '证件号',
    birthday          date comment '生日',
    id1_front_fid     varchar(128) comment '证件1-正面',
    id1_back_fid      varchar(128) comment '证件1-反面',
    id2_front_fid     varchar(128) comment '证件2-正面',
    id2_back_fid      varchar(128) comment '证件2-反面',
    real_state        varchar(2) comment '实名状态',

    -- basic
    creator           bigint comment '创建者',
    create_date       datetime comment '创建时间',
    updater           bigint comment '更新者',
    update_date       datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER
      SET utf8mb4 COMMENT = 'j_wallet';
create index idx_j_wallet_1 on j_wallet (agent_id, merchant_id, sub_id, create_date);
create index idx_j_wallet_2 on j_wallet (merchant_id, sub_id, create_date);
create index idx_j_wallet_3 on j_wallet (sub_id, create_date);
-- 唯一索引
create unique index uidx_j_wallet_0 on j_wallet (sub_id, email);
create unique index uidx_j_wallet_1 on j_wallet (sub_id, phone);
