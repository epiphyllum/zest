-- 全局配置
drop table if exists j_config;
create table j_config
(
    id               bigint      not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA

    quota_limit      int         not null default 10 comment '批量开卡的最大数量',
    vcc_main_real    varchar(30) not null comment '平台实体主卡',
    vcc_main_virtual varchar(30) not null comment '平台虚拟主卡',

    -- basic
    creator          bigint comment '创建者',
    create_date      datetime comment '创建时间',
    updater          bigint comment '更新者',
    update_date      datetime comment '更新时间',
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
    currency    varchar(4) comment '币种',
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
