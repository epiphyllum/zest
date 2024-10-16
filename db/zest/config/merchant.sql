-- 商户/子商户
drop table if exists j_merchant;
create table j_merchant
(
    id                 bigint         not null comment '商户ID',

    -- 所属代理
    agent_id           bigint,
    agent_name         varchar(50),

    -- 入网信息
    meraplid           varchar(32) comment '跟踪号',                                        --	meraplid	String	32	Y	客户自己生成，保持唯一
    cusname            varchar(30) comment '商户名称',                                      --	cusname	String	30	Y
    flag               varchar(3) comment '商户性质',                                       --	flag	String	3	Y	1: 合资、股份制、民营 2 : 世界500强或国有 : 个体户 : 个人
    buslicensename     varchar(100) comment '营业执照名称',                                 --	buslicensename	String	100	Y
    areacode           varchar(10) comment '注册地',                                        --	areacode	String	10	Y	见附录【国别信息】中的CODE,如中国：CHN
    province           varchar(10) comment '所在省份',                                      --	province	String	10	C	注册国家为中国时必填，见附件【地区代码】
    city               varchar(10) comment '所在城市',                                      -- city	String	10	C	注册国家为中国时必填，见附件【地区代码】
    address            varchar(1000) comment '注册地址',                                    --	address	String	100	Y
    cusengname         varchar(100) comment '客户英文名称',                                 --	cusengname	String	100	Y
    tel                varchar(30) comment '联系电话',                                      --	tel	String	30	Y
    legalemail         varchar(30) comment '邮箱',                                          --	legalemail	String	30	Y
    legal              varchar(30) comment '法人姓名',                                      --	legal	String	30	Y
    legalarea          varchar(10) comment '法人国籍',                                      --	legalarea	String	10	Y	见附录【国别信息】中的CODE,如中国：CHN
    legalidtype        varchar(10) comment '法人证件类型',                                  --	legalidtype	String	10	Y	01：居民身份证 02：军人或武警身份证, 03：港澳台通行证, 4：护照, 05：其他有效旅行证件 06：其他类个人有效证件
    legalidno          varchar(30) comment '法人证件号',                                    --	legalidno	String	30	Y legalidexpire varchar(30) not null, -- 法人证件有效期	legalidexpire	String	30	Y	YYYYMMDD
    legaladdress       varchar(200) comment '法人代表住址',                                 --	legaladdress	String	200	Y
    threcertflag       varchar(2) comment '是否三证合一',                                   --	threcertflag	String	2	C	0-否、1-是 注册国家/地区areacode为CHN时必填
    buslicense         varchar(10) comment '营业执照代码',                                  --	buslicense	String	10	C	注册国家/地区areacode不为CHN时，或者注册国家/地区areacode为CHN，三证不合一时，填营业执照代码
    buslicenseexpire   varchar(30) comment '营业执照有效期',                                --	buslicenseexpire	String	30	C	YYYYMMDD，注册国家/地区areacode不为CHN时，或者注册国家/地区areacode为CHN，三证不合一时，填营业执照有效期
    creditcode         varchar(30) comment '统一社会信用证代码/税务登记证代码',             --	creditcode	String	30	C	注册国家/地区areacode为CHN，三证合一时，填入统一社会信用证代码；三证不合一时，填入税务登记证代码有效期
    creditcodeexpire   varchar(30) comment '统一社会信用证代码有效期/税务登记证代码有效期', --	creditcodeexpire	String	30	C	YYYYMMDD，注册国家/地区areacode为CHN，三证合一时，填入统一社会信用证代码有效期；三证不合一时，填入税务登记证代码有效期
    organcode          varchar(30) comment '组织机构代码/公司注册证书(CR)编号',             --	organcode	String	30	C	注册国家/地区areacode为CHN，三证不合一时，填入组织机构代码
    organcodeexpire    varchar(30) comment '组织机构代码/公司注册证书(CR)编号有效期',       --	organcodeexpire	String	30	C	注册国家/地区areacode为CHN，三证不合一时，填入组织机构代码有效期
    legaloccop         varchar(30) comment '法人代表职业',                                  --	legaloccop	String	30	O
    legaltel           varchar(30) comment '法人代表手机号码',                              --	legaltel	String	30	O
    holdername         varchar(30) comment '控股股东或实际控制人姓名',                      --	holdername	String	30	O
    holderidno         varchar(30) comment '控股股东或实际控制人证件号',                    --	holderidno	String	30	O
    holderexpire       varchar(30) comment '控股股东或实际控制人证件有效期',                --	holderexpire	String	30	O	YYYYMMDD
    legalphotofrontfid varchar(200) comment '法人身份证正面',                               --	legalphotofrontfid	String	200	Y	附件fid
    legalphotobackfid  varchar(200) comment '法人身份证正面',                               --	legalphotobackfid	String	200	Y	附件fid
    agreementfid       varchar(200) comment '子卡商户合作协议',                             --	agreementfid	String	200	Y	附件fid
    creditfid          varchar(200) comment '统一社会信用证及影印件',                       --	credifid	String	200	C	注册国家/地区areacode为CHN，三证合一时，填入统一社会信用证及影印件上传文件fid
    buslicensefid      varchar(200) comment '营业执照',                                     --	buslicensefid	String	200	C	附件fid
    taxfid             varchar(200) comment '税务登记证及影印件',                           --	taxfid	String	200	C	注册国家/地区areacode为CHN，三证不合一时，填入税务登记证及影印件上传文件fid
    organfid           varchar(200) comment '组织机构代码及影印件',                         --	organfid	String	200	C	注册国家/地区areacode为CHN，三证不合一时，填入组织机构代码及影印件上传文件fid
    -- 通联应答:response
    cusid              varchar(15) comment '通联子商户号: 会从零时商户号变正式商户号',      --	cusid	String	15	A	状态为审核成功时返回
    state              varchar(2) comment '状态',                                           -- state	String	2	Y	04：审核成功; 05：审核失败; 其他情况为空;
    -- extra management:
    enabled            int            not null default 1 comment '启用',

    -- 商户接入参数
    mcc                varchar(6)     not null comment '商户类型',                          -- dict
    deposit_rate       decimal(10, 3) comment '保证金比例',
    charge_rate        decimal(10, 3) comment '充值费率',                                   --
    l50                decimal(10, 2) not null,
    gef50              decimal(10, 2) not null,
    fail_fee           decimal(10, 2) comment '失败费',
    dispute_fee        decimal(10, 2) comment '争议处理费',

    txn_rate           decimal(10, 3) comment '交易费率',
    public_key         varchar(1024)  null comment '商户公钥',
    webhook            varchar(128) comment '商户通知地址',
    white_ip           varchar(256) comment '接口IP白名单',

    -- 接口权限
    permissions        varchar(256)   not null default '{"payInfo":1,"cardWithdraw":1}',

    -- basic
    creator            bigint comment '创建者',
    create_date        datetime comment '创建时间',
    updater            bigint comment '更新者',
    update_date        datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB
  collate utf8mb4_bin
  DEFAULT CHARACTER SET utf8mb4 COMMENT ='j_merchant';
create index idx_j_merchant_1 on j_merchant (create_date);
create index idx_j_merchant_2 on j_merchant (agent_id, create_date);
create index idx_j_merchant_3 on j_merchant (cusid);

