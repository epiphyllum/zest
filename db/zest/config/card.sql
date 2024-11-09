-- 其他主卡在通联后台录入后， 得到卡号， 作为我们系统的产品配置
-- 子卡 + vpa主卡
drop table if exists j_card;
create table j_card
(
    id                   bigint         not null comment 'ID',
    -- ID相关信息
    agent_id             bigint         not null comment '代理id',
    agent_name           varchar(50)    not null comment '代理',
    merchant_id          bigint         not null comment '商户ID',
    merchant_name        varchar(50)    not null comment '商户',
    sub_id               bigint         not null comment '子商户id',
    sub_name             varchar(50)    not null comment '子商户',
    api                  int            not null default 1,

    --
    marketproduct        varchar(16) comment '对外卡产品',
    --
    maincardno           varchar(30) comment 'vpa main card no',                    --
    -- request
    cardtype             varchar(2) comment '卡片种类',                             --	cardtype	String	2	Y	1：虚拟卡，（产品类型为：通华金服VISA虚拟卡、通华VPA电子卡）4：虚实同发，（产品类型为：通华金服VISA公务卡、万商义乌VISA商务卡）
    cardholdertype       varchar(3) comment '持卡人身份',                           -- 1：法人持有 0：其他管理员
    belongtype           varchar(2) comment '主体类型',                             --	belongtype	String	2	Y	1：员工 2：合作企业
    producttype          varchar(6) comment '卡产品',                               --	cardtype	String	2	Y	1：虚拟卡，（产品类型为：通华金服VISA虚拟卡、通华VPA电子卡）4：虚实同发，（产品类型为：通华金服VISA公务卡、万商义乌VISA商务卡）
    cusid                varchar(30) comment '通联子商id',                          --   主体类型为合作企业时必填，【子商户创建】结果返回的cusid
    -- 2
    nationality          varchar(3) comment '国籍',                                 --	nationality	String	3	Y	见附录【国别信息】中的CODE,如中国：CHN
    companyposition      varchar(50) comment '公司职位',                            --	companyposition	String	50	Y	1：法人代表2：董事3：高级管理员4：经理5：职员
    -- 3
    surname              varchar(20) comment '姓氏',                                --	surname	String	20	Y
    `name`               varchar(30) comment '名字',                                --	name	String	30	Y
    birthday             varchar(10) comment '出生日期',                            --	birthday	String	10	Y	YYYYMMDD
    -- 4
    idtype               varchar(2) comment '证件1类型',                            -- idtype	String	2	Y	01：居民身份证（国籍为中国）04：护照（国籍为非中国）
    idnumber             varchar(30) comment '证件1号码',                           -- idnumber	String	30	Y
    idtype2              varchar(2) comment '证件2类型',                            --	国籍不为中国时必填01：居民身份证02：军人或武警身份证03：港澳台通行证04：护照05：其他有效旅行证件06：其他类个人有效证件
    idnumber2            varchar(30) comment '证件2号码',                           --	国籍不为中国时必填 country varchar(3), --	居住国家/地区	见附录【国别信息】中的CODE,如中国：CHN

    country              varchar(3) comment '居住国家/地区',                        --	country	String	3	Y	见附录【国别信息】中的CODE,如中国：CHN
    address              varchar(300) comment '详细地址',                           --	address	String	200	Y
    province             varchar(6) comment '省份',                                 --	居住国家/地区为CHN时必填，见附件【地区代码】
    city                 varchar(6) comment '城市',                                 --	居住国家/地区为CHN时必填，见附件【地区代码】

    email                varchar(50) comment '邮箱',                                --	email	String	50	Y
    gender               varchar(2) comment '性别',                                 --	gender	String	2	Y	1：男 0：女

    mobilecountry        varchar(10) comment '手机号码所属地区',                    --	mobilecountry	String	10	Y	见附录【国别信息】中的CODE
    mobilenumber         varchar(20) comment '手机号码',                            --	mobilenumber	String	20	Y

    photofront           varchar(100) comment '正面照片',                           --	photofront	String	100	Y
    photoback            varchar(100) comment '反面照片',                           --	photoback	String	100	Y
    photofront2          varchar(100) comment '正面照片',                           --	photofront	String	100	Y
    photoback2           varchar(100) comment '反面照片',                           --	photoback	String	100	Y

    payerid              varchar(15) comment '申请费用扣款账户',                    --	payerid	String	15	Y	【VA账户列表查询】响应报文中的账户唯一标识id

    deliverypostcode     varchar(20) comment '邮政编码',                            --
    deliverycountry      varchar(10) comment '邮寄国家/地区',                       -- 	deliverycountry	String	10	C	卡片类型为虚实同发时必填，见附录【国别信息】中的CODE
    deliveryprovince     varchar(6) comment '邮寄省份',                             -- 	deliveryprovince	String	6	C	邮寄国家/地区为CHN时必填，见附件【地区代码】
    deliverycity         varchar(6) comment '邮寄城市',                             -- 	deliverycity	String	6	C	邮寄国家/地区为CHN时必填，见附件【地区代码】
    deliveryaddress      varchar(200) comment '邮寄城市',                           -- 	deliveryaddress	String	200	C	卡片类型为虚实同发时必填

    -- 共享卡要求字段
    payeeaccount         varchar(30) comment '交易对手',                            -- 交易对手	payeeaccount	String	30	O
    procurecontent       varchar(200) comment '采购内容',                           -- 采购内容	procurecontent	String	200	O
    agmfid               varchar(100) comment '合同文件',                           -- 保证金对应合同协议	agmfid	String	100	O

    -- 大吉设计
    currency             varchar(3)     not null comment '卡的币种',
    merchantfee          decimal(18, 2) not null default 0 comment '我们收商户的费用',
    notify_count         int            not null default 0,
    notify_status        int            not null default 0 comment '0: 待通知, 1: 通知成功, 2: 通知失败',

    applyid              varchar(32) comment '申请ID',                              --
    meraplid             varchar(32) comment '申请单流水',                          -- 	meraplid	String	32	Y	客户自己生成，保持唯一
    txnid                varchar(32) comment '平台流水号',

    -- notify:
    fee                  decimal(18, 2) comment '申请费用(通联收费)',               -- fee	Number	18,2	O
    feecurrency          varchar(3) comment '申请费用币种',                         -- 	feecurrency	String	3	O
    cardno               varchar(30) comment '卡号',                                -- 	cardno	String	30	O	申请成功后返回
    state                varchar(2)              default '00' comment '卡申请状态', --
    stateexplain          varchar(200) comment '解释',
    cardstate           varchar(2)              default '00' comment '',           -- '卡状态'
    balance              decimal(18, 2) comment '卡余额',                           -- 可以收到授权通知后查询

    --
    cvv                  varchar(32),
    expiredate           varchar(32),

    -- 场景相关信息
    sceneid              varchar(30),
    scenename            varchar(50),
    cycle                varchar(1),
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
    -- 允许币种
    permit_currency      varchar(60),

    -- vpa子卡字段
    vpa_job              bigint comment '那个vpa的卡ID',

    -- 可用授权额度:
    prepaid_available    decimal(18, 2),

    -- basic
    creator              bigint comment '创建者',
    create_date          datetime comment '创建时间',
    updater              bigint comment '更新者',
    update_date          datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_card';
create index idx_j_card_1 on j_card (merchant_id, create_date);
create index idx_j_card_2 on j_card (sub_id, create_date);
create index idx_j_card_3 on j_card (agent_id, create_date);
create index idx_j_card_4 on j_card (applyid);
create index idx_j_card_5 on j_card (merchant_id, meraplid);
-- 唯一索引
create unique index uidx_j_card_0 on j_card (txnid);
