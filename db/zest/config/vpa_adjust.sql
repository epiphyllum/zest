-- 卡额度管理:
drop table if exists j_vpa_adjust;
create table j_vpa_adjust
(
    id            bigint         not null comment 'ID',
    agent_id      bigint         not null comment '代理id',
    agent_name    varchar(50)    not null comment '代理',
    merchant_id   bigint         not null comment '商户ID', -- 商户ID
    merchant_name varchar(50)    not null comment '商户',   -- 商户
    sub_id        bigint         not null comment '子商户ID',
    sub_name      varchar(50)    not null comment '子商户',
    -- 业务字段
    maincardno    varchar(30)    not null comment '主卡卡号',
    cardno        varchar(30)    not null comment 'vpa子卡',
    adjust_amount decimal(18, 2) not null comment '调整金额',
    old_quota     decimal(18, 2) not null comment '调整前额度',
    new_quota     decimal(18, 2) not null comment '调整后额度',
    state         varchar(2)     not null default '00' comment '调整状态',

    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_vpa_adjust';
create index idx_j_vpa_adjust_1 on j_vpa_adjust (agent_id, merchant_id, sub_id, create_date);
create index idx_j_vpa_adjust_2 on j_vpa_adjust (merchant_id, sub_id, create_date);
create index idx_j_vpa_adjust_3 on j_vpa_adjust (sub_id, create_date);
