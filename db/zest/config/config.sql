-- 全局配置
drop table if exists j_config;
create table j_config
(
    id          bigint not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA

    deposit_rate decimal(3, 3) not null comment '保证金扣率',
    charge_rate decimal(3, 3) not null comment '手续费扣率',

    -- basic
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_config';
