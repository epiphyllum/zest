drop table if exists j_fee_config;
create table j_fee_config
(
    id                bigint         not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA
    agent_id          bigint         not null comment '代理ID',
    agent_name        varchar(50)    not null comment '代理名称',
    merchant_id       bigint         not null comment '商户ID',
    merchant_name     varchar(50)    not null comment '商户名称',
    marketproduct     varchar(16)    not null comment '产品',
    -- 成本
    cost_l50          decimal(18, 2) not null comment '小金额手续费每笔',
    cost_gef50        decimal(18, 2) not null comment '>=50失败手续费',
    cost_fail_fee     decimal(18, 2) not null comment '失败费/笔, 当失败率>15%',
    cost_dispute_fee  decimal(18, 2) not null comment '争议处理费',
    cost_card_fee     decimal(18, 2) not null comment '开卡费',
    -- 收入
    l50               decimal(18, 2) not null comment '小金额手续费每笔',
    gef50             decimal(18, 2) not null comment '>=50 fail 手续费',
    fail_fee          decimal(18, 2) not null comment '失败费/笔, faiL_rate > 15%',
    dispute_fee       decimal(18, 2) not null comment '争议处理费',
    card_fee          decimal(18, 2) not null comment '开卡费',
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

