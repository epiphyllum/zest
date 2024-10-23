-- j_free: 全部是子商户的: 担保金释放
drop table if exists j_free;
create table j_free
(
    id          bigint not null,
    amount      decimal(18, 6) comment '交易金额', -- 交易金额	amount	Number	18,6	Y
    currency    varchar(3) comment '币种',         -- 交易币种	currency	String	3	Y
    applyid     varchar(32),

    -- basic
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_free';
create unique index uidx_jfree_1 on j_free (applyid);
