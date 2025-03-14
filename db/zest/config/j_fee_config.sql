-- 商户成本收入配置
drop table if exists j_fee_config;
create table j_fee_config
(
    id                bigint         not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA
    agent_id          bigint         not null comment '代理ID',
    agent_name        varchar(50)    not null comment '代理名称',
    merchant_id       bigint         not null comment '商户ID',
    merchant_name     varchar(50)    not null comment '商户名称',
    marketproduct     varchar(16)    not null comment '产品',
    currency          varchar(3)     not null comment '本币币种',

    -- 成本
    cost_charge_rate  decimal(18,3)  not null comment '',
    cost_deposit_rate decimal(18, 3) not null comment '',
    cost_l50          decimal(18, 3) not null comment '小金额手续费每笔(美元？)',
    cost_gef50        decimal(18, 3) not null comment '>=50失败手续费(美元？)',
    cost_fail_fee     decimal(18, 3) not null comment '失败费/笔, 当失败率>15%',
    cost_dispute_fee  decimal(18, 3) not null comment '争议处理费(美元？)',
    cost_card_fee     decimal(18, 3) not null comment '开卡费(美元？)',

    -- 收入
    charge_rate  decimal(18,3) not null comment '卡充值扣率',
    deposit_rate decimal(18,3) not null comment '保证金扣率',
    l50               decimal(18, 3) not null comment '小金额手续费每笔(美元?)',
    gef50             decimal(18, 3) not null comment '>=50 fail 手续费(美元?)',
    fail_fee          decimal(18, 3) not null comment '失败费/笔, faiL_rate > 15%',
    dispute_fee       decimal(18, 3) not null comment '争议处理费(美元?)',
    card_fee          decimal(18, 3) not null comment '开卡费(按币种)',

    -- basic
    creator           bigint comment '创建者',
    create_date       datetime comment '创建时间',
    updater           bigint comment '更新者',
    update_date       datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_fee_config';
create unique index uidx_jfeeconfig_1 on j_fee_config(merchant_id, marketproduct);

