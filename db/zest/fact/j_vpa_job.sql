-- 共享子卡申请流水
drop table if exists j_vpa_job;
create table j_vpa_job
(
    id                   bigint         not null comment 'ID',
    agent_id             bigint         not null comment '代理id',
    agent_name           varchar(50)    not null comment '代理',
    merchant_id          bigint         not null comment '商户ID', -- 商户ID
    merchant_name        varchar(50)    not null comment '商户',   -- 商户
    sub_id               bigint         not null comment '子商户ID',
    sub_name             varchar(50)    not null comment '子商户',
    api                  int            not null default 1,
    -- 其实是直接创建后不用的
    sceneid              varchar(30)    not null comment '场景ID: 通联返回',
    -- 场景相关信息: scenename 随便用个序列号就好了
    scenename            varchar(50),
    --
    cycle                varchar(1) comment '1:期限，2:周期，3:单次',
    currency             varchar(30) comment '允许币种: 多个用逗号分开',
    authmaxcount         int,
    authmaxamount        decimal(18, 2),
    onlhkflag            varchar(1),
    -- 期限卡
    begindate            varchar(10),
    enddate              varchar(10),
    -- 周期卡
    naturalmonthflag     varchar(1),
    naturalmonthstartday varchar(2),
    -- 单次卡
    fixedamountflag      varchar(1),
    -- 任务状态: 需要查询同类后台获取所有子卡信息以及cvv并插入到卡表
    state                varchar(2)     not null default '00' comment '批量开卡的任务状态',
    merchantfee          decimal(18, 2) not null default 0 comment '发卡手续费',
    feecurrency          varchar(3)     not null default 0 comment '通联手续费币种',
    -- 创建相关信息
    maincardno           varchar(30)    not null comment '主卡(共享主卡)',
    maincardid           long           not null comment '主卡ID',
    num                  int            not null comment '开卡数量',
    email                varchar(30)    not null comment '邮箱',
    cardexpiredate       varchar(10)    not null comment '卡有效期',

    marketproduct        varchar(16)    not null comment '产品',
    productcurrency      varchar(3)     not null comment '本币币种',
    wallet_id            bigint comment '钱包ID',
    wallet_name          varchar(64) comment '钱包',

    -- 接口
    meraplid             varchar(32)    not null comment '商户发起的meraplid, 我们用id',
    applyid              varchar(32)    null comment '通联返回',
    -- basic
    stat_date            date comment '',
    creator              bigint comment '创建者',
    create_date          datetime comment '创建时间',
    updater              bigint comment '更新者',
    update_date          datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_vpa_job';
create index idx_j_vpa_job_1 on j_vpa_job (merchant_id, create_date);
create index idx_j_vpa_job_2 on j_vpa_job (applyid);
-- 唯一索引
create unique index uidx_j_vpa_job_1 on j_vpa_job (sub_id, meraplid);
