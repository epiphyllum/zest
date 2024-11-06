-- 全局配置
drop table if exists j_config;
create table j_config
(
    id           bigint        not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA
    deposit_rate decimal(3, 3) not null comment '保证金扣率',
    charge_rate  decimal(3, 3) not null comment '手续费扣率',
    l50          decimal(18,2) not null comment '小金额手续费每笔',
    gef50        decimal(18,2) not null comment '>=50 fail 手续费',
    fail_fee     decimal(18,2) not null comment '失败费/笔, faiL_rate > 15%',
    dispute_fee  decimal(18,2) not null comment '争议处理费',
    quota_limit  int           not null default 10 comment '批量开卡的最大数量',

    -- basic
    creator      bigint comment '创建者',
    create_date  datetime comment '创建时间',
    updater      bigint comment '更新者',
    update_date  datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_config';

-- 卡产品
drop table if exists j_card_fee_config;
create table j_card_fee_config
(
    id          bigint not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA

    -- 卡开收费配置
    producttype varchar(15) comment '产品类型',
    cardtype    varchar(2) comment '卡类型',
    currency    varchar(3) comment '币种',
    fee         decimal(18, 2) comment '收费',

    -- basic
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_card_fee_config';
