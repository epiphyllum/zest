-- 其他主卡在通联后台录入后， 得到卡号， 作为我们系统的产品配置
-- 子卡 + vpa主卡
drop table if exists j_batch;
create table j_batch
(
    id          bigint       not null comment 'ID',
    batch_type  varchar(64)  not null comment '任务名称',
    batch_args  varchar(128) not null comment '任务参数',
    state       varchar(2)   not null comment '任务状态',

    -- basic
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_batch';
create index idx_j_agent_1 on j_agent (create_date);

