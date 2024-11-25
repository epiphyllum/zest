-- j_free: 通联释放担保金
drop table if exists j_free;
create table j_free
(
    id          bigint     not null,

    currency    varchar(3) not null comment '币种',
    amount      decimal(18, 6) comment '交易金额', -- 交易金额	amount	Number	18,6	Y
    applyid     varchar(32),

    -- basic
    stat_date        date comment '完成日期',
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_free';
create unique index uidx_jfree_1 on j_free (applyid);
