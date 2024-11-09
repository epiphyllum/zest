-- 钱包管理
drop table if exists j_wallet;
create table j_wallet
(
    id            bigint      not null comment 'ID',
    agent_id      bigint      not null comment '代理id',
    agent_name    varchar(50) not null comment '代理',
    merchant_id   bigint      not null comment '商户ID', -- 商户ID
    merchant_name varchar(50) not null comment '商户',   -- 商户
    sub_id        bigint      not null comment '子商户ID',
    sub_name      varchar(50) not null comment '子商户',

    wallet_name   varchar(50) not null comment '钱包名称: sys_user.username',

    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_wallet';
create index idx_j_wallet_1 on j_wallet (agent_id, merchant_id, sub_id, create_date);
create index idx_j_wallet_2 on j_wallet (merchant_id, sub_id, create_date);
create index idx_j_wallet_3 on j_wallet (sub_id, create_date);

