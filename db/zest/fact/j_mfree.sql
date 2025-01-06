-- j_mfree: 全部是子商户的: 担保金释放
drop table if exists j_mfree;
create table j_mfree
(

    id            bigint      not null,
    agent_id      bigint      not null,
    agent_name    varchar(50) not null,
    merchant_id   bigint      not null,
    merchant_name varchar(50) not null,
    sub_id        bigint      not null,
    sub_name      varchar(50) not null,
    marketproduct varchar(16) not null,
    currency      varchar(3)  not null,

    -- basic
    stat_date        date comment '完成日期',
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_free';
create index idx_j_mfree_1 on j_mfree (agent_id, create_date);
create index idx_j_mfree_2 on j_mfree (merchant_id, create_date);
