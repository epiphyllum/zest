-- 商户日志
drop table if exists j_packet;
create table j_packet
(
    id             bigint         not null,
    agent_id       bigint         not null comment '代理ID',
    agent_name     varchar(32)    not null comment '代理名',
    merchant_id    bigint         not null comment '商户ID',
    merchant_name  varchar(32)    not null comment '商户名',
    req_id         varchar(64)   not null comment '请求ID',
    api_name       varchar(32)   not null comment '接口名称',
    recv           varchar(2048) not null comment '报文内容',
    send           varchar(2048) not null comment '报文内容',
    sign           varchar(1024) not null comment '签名',
    ip             varchar(15)  comment 'IP',
    -- basic(4)
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_packet';
create index idx_j_packet_1 on j_packet(req_id);

-- 通联日志
drop table if exists j_channel_log;
create table j_channel_log
(
    id             bigint         not null,
    req_id         varchar(64)   not null comment '请求ID',
    api_name       varchar(64)   not null comment '接口名称',
    recv           varchar(2048) not null comment '收到',
    send           varchar(2048) not null comment '发送',
    sign           varchar(1024) not null comment '签名',
    ip             varchar(15) null comment 'IP',
    -- basic(4)
    creator        bigint comment '创建者',
    create_date    datetime comment '创建时间',
    updater        bigint comment '更新者',
    update_date    datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_channel_log';
create index idx_j_tlog_1 on j_channel_log(req_id);
