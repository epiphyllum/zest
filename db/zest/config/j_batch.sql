-- 批处理任务
drop table if exists j_batch;
create table j_batch
(
    id          bigint       not null comment 'ID',
    batch_type  varchar(64)  not null comment '任务名称',
    state       varchar(2)   not null comment '任务状态',
    batch_date  varchar(10)  not null comment '任务日期',
    memo        varchar(200) comment '备注',

    -- basic
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_batch';
create index idx_j_batch_1 on j_batch (create_date);
create unique index uidx_j_batch_1 on j_batch(batch_type, batch_date)
