-- 充值渠道
drop table if exists j_pay_channel;
create table j_pay_channel
(
    id            bigint         not null comment 'ID',
    agent_id      bigint         not null comment '代理id',
    agent_name    varchar(50)    not null comment '代理',
    merchant_id   bigint         not null comment '商户ID', -- 商户ID
    merchant_name varchar(50)    not null comment '商户',   -- 商户
    sub_id        bigint         not null comment '子商户ID',
    sub_name      varchar(50)    not null comment '子商户',

    enabled       int        not null default 1 comment '启用',
    channel_code  varchar(50)    not null comment '渠道代码',
    channel_name  varchar(50)    not null comment '渠道名称',
    merchant_no   varchar(32)    not null comment '商户号',
    stl_currency  varchar(3)     not null comment '结算币种',
    api_url       varchar(256)   not null comment '接口地址',
    public_key    varchar(1024) comment '我方公钥',
    private_key   varchar(1024) comment '我方私钥',
    channel_key   varchar(1024) comment '渠道公钥',

    charge_rate   decimal(18, 3) not null comment '扣率',
    floor         decimal(18, 2) not null default 0 comment '保底',
    ceiling       decimal(18, 2) not null default 9999999999 comment '封顶',
    weight        int not null default 100 comment '权重',

    -- basic
    creator       bigint comment '创建者',
    create_date   datetime comment '创建时间',
    updater       bigint comment '更新者',
    update_date   datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_pay_channel';
create index idx_j_pay_channel_1 on j_pay_channel (agent_id, merchant_id, sub_id, create_date);
create index idx_j_pay_channel_2 on j_pay_channel (merchant_id, sub_id, create_date);
create index idx_j_pay_channel_3 on j_pay_channel (sub_id, create_date);
