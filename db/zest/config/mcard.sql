-- 主卡
drop table if exists j_mcard;
create table j_mcard
(
    id               bigint      not null comment 'ID',

    agent_id         bigint      not null comment '代理id',
    agent_name       varchar(50) not null comment '代理',

    merchant_id      bigint      not null comment '商户ID',        -- 商户ID
    merchant_name    varchar(32) not null comment '商户',          -- 商户

    -- request
    meraplid         varchar(32) comment '申请单流水',             -- 	meraplid	String	32	Y	客户自己生成，保持唯一
    cardtype         varchar(2) comment '卡片种类',                --	cardtype	String	2	Y	1：虚拟卡，（产品类型为：通华金服VISA虚拟卡、通华VPA电子卡）4：虚实同发，（产品类型为：通华金服VISA公务卡、万商义乌VISA商务卡）

    cardholdertype   varchar(3) comment '持卡人身份',              -- 1：法人持有 0：其他管理员
    nationality      varchar(3) comment '国籍',                    --	nationality	String	3	Y	见附录【国别信息】中的CODE,如中国：CHN
    companyposition  varchar(50) comment '公司职位',               --	companyposition	String	50	Y	1：法人代表2：董事3：高级管理员4：经理5：职员

    surname          varchar(20) comment '姓氏',                   --	surname	String	20	Y
    `name`           varchar(30) comment '名字',                   --	name	String	30	Y
    birthday         varchar(10) comment '出生日期',               --	birthday	String	10	Y	YYYYMMDD

    idtype           varchar(2) comment '证件1类型',               -- idtype	String	2	Y	01：居民身份证（国籍为中国）04：护照（国籍为非中国）
    idnumber         varchar(30) comment '证件1号码',              -- idnumber	String	30	Y
    idtype2          varchar(2) comment '证件2类型',               --	国籍不为中国时必填01：居民身份证02：军人或武警身份证03：港澳台通行证04：护照05：其他有效旅行证件06：其他类个人有效证件
    idnumber2        varchar(30) comment '证件2号码',              --	国籍不为中国时必填 country varchar(3), --	居住国家/地区	见附录【国别信息】中的CODE,如中国：CHN

    country          varchar(3) comment '居住国家/地区',           --	country	String	3	Y	见附录【国别信息】中的CODE,如中国：CHN
    address          varchar(300) comment '详细地址',              --	address	String	200	Y
    province         varchar(6) comment '省份',                    --	居住国家/地区为CHN时必填，见附件【地区代码】
    city             varchar(6) comment '城市',                    --	居住国家/地区为CHN时必填，见附件【地区代码】

    email            varchar(50) comment '邮箱',                   --	email	String	50	Y
    gender           varchar(2) comment '性别',                    --	gender	String	2	Y	1：男 0：女

    mobilecountry    varchar(10) comment '手机号码所属地区',       --	mobilecountry	String	10	Y	见附录【国别信息】中的CODE
    mobilenumber     varchar(20) comment '手机号码',               --	mobilenumber	String	20	Y


    photofront       varchar(100) comment '正面照片',              --	photofront	String	100	Y
    photoback        varchar(100) comment '反面照片',              --	photoback	String	100	Y
    photofront2      varchar(100) comment '正面照片',              --	photofront	String	100	Y
    photoback2       varchar(100) comment '反面照片',              --	photoback	String	100	Y

    payerid          varchar(15) comment '申请费用扣款账户',       --	payerid	String	15	Y	【VA账户列表查询】响应报文中的账户唯一标识id

    deliverypostcode varchar(20) comment '邮政编码',               --
    deliverycountry  varchar(10) comment '邮寄国家/地区',          -- 	deliverycountry	String	10	C	卡片类型为虚实同发时必填，见附录【国别信息】中的CODE
    deliveryprovince varchar(6) comment '邮寄省份',                -- 	deliveryprovince	String	6	C	邮寄国家/地区为CHN时必填，见附件【地区代码】
    deliverycity     varchar(6) comment '邮寄城市',                -- 	deliverycity	String	6	C	邮寄国家/地区为CHN时必填，见附件【地区代码】
    deliveryaddress  varchar(200) comment '邮寄城市',              -- 	deliveryaddress	String	200	C	卡片类型为虚实同发时必填

    -- 主卡(额为4个字段)
    payeeaccount     varchar(30),                                  -- 卡产品类型为：021201-通华VPA电子卡时必填
    procurecontent   varchar(100),                                 -- 卡产品类型为：021201-通华VPA电子卡时必填
    agmfid           varchar(100),                                 -- 卡产品类型为：021201-通华VPA电子卡时必填
    producttype      varchar(3) comment '产品类型',                -- 001001:通华金服VISA公务卡011001 : 万商义乌VISA商务卡001201 : 通华金服VISA虚拟卡021201 : 通华VPA电子卡

    -- response
    applyid          varchar(32) comment '申请ID',                 --

    -- notify:
    fee              decimal(18, 2) comment '申请费用',            -- fee	Number	18,2	O
    feecurrency      varchar(3) comment '申请费用币种',            -- 	feecurrency	String	3	O
    cardno           varchar(30) comment '卡号',                   -- 	cardno	String	30	O	申请成功后返回
    state            varchar(2) default '00' comment '卡申请状态', --

    -- basic
    creator          bigint comment '创建者',
    create_date      datetime comment '创建时间',
    updater          bigint comment '更新者',
    update_date      datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_mcard';
create index idx_j_card_1 on j_mcard (merchant_id, create_date);

