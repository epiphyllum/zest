-- 资金调度
drop table if exists j_allocate;
create table j_allocate
(
    id            bigint         not null comment 'ID',

    agent_id      bigint         not null comment '代理ID',
    agent_name    varchar(50)    not null comment '代理名称',
    merchant_id   bigint         not null comment '商户ID',
    merchant_name varchar(50)    not null comment '商户名称',
    sub_id        bigint         null comment '子商户ID',
    sub_name      varchar(50)    null comment '子商户名称',

    -- 业务字段
    api            int not null default 1,
    meraplid      varchar(64)    not null comment '商户单号',
    type          varchar(32)    not null comment 'm2s, s2m',
    amount        decimal(18, 2) not null comment '金额',
    currency      varchar(3)     not null comment '币种',

    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_allocate';
create index idx_j_allocate_1 on j_allocate (merchant_id, create_date);
create index idx_j_allocate_2 on j_allocate (sub_id, create_date);
create index idx_j_allocate_3 on j_allocate (agent_id, create_date);
