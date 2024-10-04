-- 大吉va账户
drop table if exists j_va;
create table j_va
(
    id          bigint not null comment 'ID', -- 这个就是商户ID/子商户ID,  同时也是商户/子商户的VA
    -- 账户信息
    tid         varchar(18),                  -- 通联id
    accountno   varchar(120),                 -- 	通联虚拟户String	120	Y
    currency    varchar(3),                   --	String	3	Y
    amount      decimal(18, 2),               --	Number	18,2	Y
    vaaccountno varchar(30),                  --	String	30	O

    -- basic
    creator     bigint comment '创建者',
    create_date datetime comment '创建时间',
    updater     bigint comment '更新者',
    update_date datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_va';
create index idx_j_va_1 on j_va (create_date);
create unique index uidx_j_va_1 on j_va (accountno);
