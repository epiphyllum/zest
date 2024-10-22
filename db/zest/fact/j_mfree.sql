-- j_free: 全部是子商户的: 担保金释放
drop table if exists j_free;
create table j_free
(
    id            bigint         not null,

    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_free';
create index idx_j_free_1 on j_free (agent_id, create_date);
create index idx_j_free_2 on j_free (merchant_id, create_date);
