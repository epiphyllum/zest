create table sys_user
(
  id                   bigint not null comment 'id',
  username             varchar(50) comment '用户名',
  password             varchar(100) comment '密码',
  real_name            varchar(50) comment '姓名',
  head_url             varchar(200) comment '头像',
  gender               tinyint(4) unsigned comment '性别   0：男   1：女    2：保密',
  email                varchar(100) comment '邮箱',
  mobile               varchar(20) comment '手机号',
  dept_id              bigint comment '部门ID',
  super_admin          tinyint unsigned comment '超级管理员   0：否   1：是',
  status               tinyint COMMENT '状态  0：停用   1：正常',
  remark               varchar(200) comment '备注',
  del_flag             tinyint(4) unsigned comment '删除标识  0：未删除    1：删除',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  updater              bigint comment '更新者',
  update_date          datetime comment '更新时间',
  primary key (id),
  unique key uk_username (username),
  key idx_del_flag (del_flag),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='用户管理';

CREATE TABLE sys_user_token
(
    id                    bigint not null comment 'id',
    user_id               bigint comment '用户ID',
    access_token          varchar(32) comment 'accessToken',
    access_token_expire   datetime comment 'accessToken 过期时间',
    refresh_token         varchar(32) comment 'refreshToken',
    refresh_token_expire  datetime comment 'refreshToken 过期时间',
    create_date           datetime comment '创建时间',
    primary key (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户Token';


create table sys_dept
(
  id                   bigint not null comment 'id',
  pid                  bigint comment '上级ID',
  pids                 varchar(500) comment '所有上级ID，用逗号分开',
  name                 varchar(50) comment '部门名称',
  leader_id            bigint COMMENT '负责人ID',
  sort                 int unsigned comment '排序',
  del_flag             tinyint(4) unsigned comment '删除标识  0：未删除    1：删除',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  updater              bigint comment '更新者',
  update_date          datetime comment '更新时间',
  primary key (id),
  key idx_pid (pid),
  key idx_del_flag (del_flag),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='部门管理';


create table sys_menu
(
  id                   bigint not null comment 'id',
  pid                  bigint comment '上级ID，一级菜单为0',
  url                  varchar(200) comment '菜单URL',
  menu_type            tinyint unsigned comment '类型   0：菜单   1：按钮',
  open_style           tinyint unsigned COMMENT '打开方式   0：内部   1：外部',
  icon                 varchar(50) comment '菜单图标',
  permissions          varchar(200) comment '权限标识，如：sys:menu:save',
  sort                 int(11) comment '排序',
  del_flag             tinyint(4) unsigned comment '删除标识  0：未删除    1：删除',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  updater              bigint comment '更新者',
  update_date          datetime comment '更新时间',
  primary key (id),
  key idx_pid (pid),
  key idx_del_flag (del_flag),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='菜单管理';


create table sys_role
(
  id                   bigint not null comment 'id',
  name                 varchar(32) comment '角色名称',
  remark               varchar(100) comment '备注',
  del_flag             tinyint(4) unsigned comment '删除标识  0：未删除    1：删除',
  dept_id              bigint comment '部门ID',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  updater              bigint comment '更新者',
  update_date          datetime comment '更新时间',
  primary key (id),
  key idx_dept_id (dept_id),
  key idx_del_flag (del_flag),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色管理';


create table sys_role_user
(
  id                   bigint not null comment 'id',
  role_id              bigint comment '角色ID',
  user_id              bigint comment '用户ID',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  primary key (id),
  key idx_role_id (role_id),
  key idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色用户关系';


create table sys_role_menu
(
  id                   bigint not null comment 'id',
  role_id              bigint comment '角色ID',
  menu_id              bigint comment '菜单ID',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  primary key (id),
  key idx_role_id (role_id),
  key idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色菜单关系';


create table sys_role_data_scope
(
  id                   bigint not null comment 'id',
  role_id              bigint comment '角色ID',
  dept_id              bigint comment '部门ID',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  primary key (id),
  key idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色数据权限';


-- 字典类型
create table sys_dict_type
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type            varchar(100) NOT NULL COMMENT '字典类型',
    dict_name            varchar(255) NOT NULL COMMENT '字典名称',
    remark               varchar(255) COMMENT '备注',
    sort                 int unsigned COMMENT '排序',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    UNIQUE KEY(dict_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='字典类型';

-- 字典数据
create table sys_dict_data
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type_id         bigint NOT NULL COMMENT '字典类型ID',
    dict_label           varchar(255) NOT NULL COMMENT '字典标签',
    dict_value           varchar(255) COMMENT '字典值',
    remark               varchar(255) COMMENT '备注',
    sort                 int unsigned COMMENT '排序',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    unique key uk_dict_type_value (dict_type_id, dict_value),
    key idx_sort (sort)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='字典数据';

-- 行政区域
CREATE TABLE sys_region (
    id bigint NOT NULL COMMENT 'id',
    pid bigint COMMENT '上级ID，一级为0',
    name varchar(100) COMMENT '名称',
    tree_level tinyint COMMENT '层级',
    leaf tinyint COMMENT '是否叶子节点  0：否   1：是',
    sort bigint COMMENT '排序',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='行政区域';

create table sys_params
(
  id                   bigint not null comment 'id',
  param_code           varchar(32) comment '参数编码',
  param_value          varchar(2000) comment '参数值',
  param_type           tinyint(4) unsigned default 1 comment '类型   0：系统参数   1：非系统参数',
  remark               varchar(200) comment '备注',
  del_flag             tinyint(4) unsigned comment '删除标识  0：未删除    1：删除',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  updater              bigint comment '更新者',
  update_date          datetime comment '更新时间',
  primary key (id),
  unique key uk_param_code (param_code),
  key idx_del_flag (del_flag),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='参数管理';


create table sys_log_login
(
  id                   bigint not null comment 'id',
  operation            tinyint unsigned comment '用户操作',
  user_agent           varchar(500) comment '用户代理',
  ip                   varchar(160) comment '操作IP',
  creator_name         varchar(50) comment '用户名',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  primary key (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='登录日志';


create table sys_log_operation
(
  id                   bigint not null comment 'id',
  module               varchar(32) comment '模块名称，如：sys',
  operation            varchar(50) comment '用户操作',
  request_uri          varchar(200) comment '请求URI',
  request_method       varchar(20) comment '请求方式',
  request_params       text comment '请求参数',
  request_time         int unsigned not null comment '请求时长(毫秒)',
  user_agent           varchar(500) comment '用户代理',
  ip                   varchar(160) comment '操作IP',
  status               tinyint(4) unsigned not null comment '状态  0：失败   1：成功',
  creator_name         varchar(50) comment '用户名',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  primary key (id),
  key idx_module (module),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='操作日志';


create table sys_log_error
(
  id                   bigint not null comment 'id',
  module               varchar(50) comment '模块名称，如：sys',
  request_uri          varchar(200) comment '请求URI',
  request_method       varchar(20) comment '请求方式',
  request_params       text comment '请求参数',
  user_agent           varchar(500) comment '用户代理',
  ip                   varchar(160) comment '操作IP',
  error_info           text comment '异常信息',
  creator              bigint comment '创建者',
  create_date          datetime comment '创建时间',
  primary key (id),
  key idx_module (module),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='异常日志';


CREATE TABLE sys_language (
  table_name varchar(32) NOT NULL COMMENT '表名',
  table_id bigint NOT NULL COMMENT '表主键',
  field_name varchar(32) NOT NULL COMMENT '字段名',
  field_value varchar(200) NOT NULL COMMENT '字段值',
  language varchar(10) NOT NULL COMMENT '语言',
  primary key (table_name, table_id, field_name, language),
  key idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='国际化';

-- 新闻管理
CREATE TABLE tb_news (
 id bigint NOT NULL COMMENT 'id',
 title varchar(255) NOT NULL COMMENT '标题',
 content mediumtext NOT NULL COMMENT '内容',
 pub_date datetime COMMENT '发布时间',
 dept_id bigint COMMENT '创建者dept_id',
 creator bigint COMMENT '创建者',
 create_date datetime COMMENT '创建时间',
 updater bigint COMMENT '更新者',
 update_date datetime COMMENT '更新时间',
 PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='新闻管理';

-- 通知管理
CREATE TABLE sys_notice (
    id bigint NOT NULL COMMENT 'id',
    notice_type int NOT NULL COMMENT '通知类型',
    title varchar(200) COMMENT '标题',
    content text COMMENT '内容',
    receiver_type tinyint unsigned COMMENT '接收者  0：全部  1：部门',
    receiver_type_ids varchar(500) COMMENT '接收者ID，用逗号分开',
    status tinyint unsigned COMMENT '发送状态  0：草稿  1：已发布',
    sender_name varchar(50) COMMENT '发送者',
    sender_date datetime COMMENT '发送时间',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    PRIMARY KEY (id),
    key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='通知管理';

-- 我的通知
CREATE TABLE sys_notice_user (
    receiver_id bigint NOT NULL COMMENT '接收者ID',
    notice_id bigint NOT NULL COMMENT '通知ID',
    read_status tinyint unsigned COMMENT '阅读状态  0：未读  1：已读',
    read_date datetime COMMENT '阅读时间',
    PRIMARY KEY (receiver_id, notice_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='我的通知';

CREATE TABLE tb_product (
    id bigint NOT NULL COMMENT 'id',
    name varchar(100) NOT NULL COMMENT '产品名称',
    content mediumtext NOT NULL COMMENT '产品介绍',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='产品管理';

CREATE TABLE tb_product_params (
    id bigint NOT NULL COMMENT 'id',
    param_name varchar(100) COMMENT '参数名',
    param_value varchar(200) COMMENT '参数值',
    product_id bigint COMMENT '产品ID',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='产品参数管理';

CREATE TABLE sys_post (
    id bigint NOT NULL COMMENT 'id',
    post_code varchar(100) COMMENT '岗位编码',
    post_name varchar(100) COMMENT '岗位名称',
    sort int COMMENT '排序',
    status tinyint COMMENT '状态  0：停用   1：正常',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='岗位管理';

CREATE TABLE sys_user_post (
    id                   bigint NOT NULL COMMENT 'id',
    post_id              bigint COMMENT '岗位ID',
    user_id              bigint COMMENT '用户ID',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    primary key (id),
    key idx_post_id (post_id),
    key idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='用户岗位关系';


CREATE TABLE tb_excel_data (
    id bigint NOT NULL COMMENT 'id',
    real_name varchar(100) COMMENT '学生姓名',
    user_identity varchar(100) COMMENT '身份证',
    address varchar(200) COMMENT '家庭地址',
    join_date datetime COMMENT '入学日期',
    class_name varchar(100) COMMENT '班级名称',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='Excel导入演示';


CREATE TABLE tb_order (
    id bigint NOT NULL COMMENT 'id',
    order_id bigint COMMENT '订单ID',
    product_id bigint NOT NULL COMMENT '产品ID',
    product_name varchar(100) COMMENT '产品名称',
    pay_amount decimal(10,2) NOT NULL COMMENT '支付金额',
    status tinyint COMMENT '订单状态  -1：已取消   0：等待付款   1：已完成',
    user_id bigint COMMENT '购买用户ID',
    pay_at datetime COMMENT '支付时间',
    create_date datetime COMMENT '下单时间',
    primary key (id),
    unique key uk_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

CREATE TABLE tb_alipay_notify_log (
    id bigint NOT NULL COMMENT 'id',
    out_trade_no bigint COMMENT '订单号',
    total_amount decimal(10,2) COMMENT '订单金额',
    buyer_pay_amount decimal(10,2) COMMENT '付款金额',
    receipt_amount decimal(10,2) COMMENT '实收金额',
    invoice_amount decimal(10,2) COMMENT '开票金额',
    notify_id varchar(50) COMMENT '通知校验ID',
    buyer_id varchar(50) COMMENT '买家支付宝用户号',
    seller_id varchar(50) COMMENT '卖家支付宝用户号',
    trade_no varchar(50) COMMENT '支付宝交易号',
    trade_status varchar(50) COMMENT '交易状态',
    create_date datetime COMMENT '创建时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付宝回调日志';

CREATE TABLE tb_wechat_notify_log (
    id bigint NOT NULL COMMENT 'id',
    out_trade_no varchar(100) COMMENT '订单号',
    total int COMMENT '订单总金额，单位为分',
    payer_total int COMMENT '用户支付金额，单位为分',
    currency varchar(50) COMMENT 'CNY：人民币，境内商户号仅支持人民币',
    payer_currency varchar(50) COMMENT '用户支付币种',
    bank_type varchar(50) COMMENT '银行类型',
    trade_state varchar(50) COMMENT '交易状态',
    trade_state_desc varchar(500) COMMENT '交易状态描述',
    trade_type varchar(50) COMMENT '交易类型',
    transaction_id varchar(100) COMMENT '微信支付系统生成的订单号',
    success_time varchar(100) COMMENT '支付完成时间',
    create_date datetime COMMENT '创建时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信回调日志';

CREATE TABLE mp_account (
    id bigint NOT NULL COMMENT 'id',
    name varchar(100) COMMENT '名称',
    app_id varchar(100) COMMENT 'AppID',
    app_secret varchar(100) COMMENT 'AppSecret',
    token varchar(100) COMMENT 'Token',
    aes_key varchar(100) COMMENT 'EncodingAESKey',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='公众号账号管理';

CREATE TABLE mp_menu (
    id bigint NOT NULL COMMENT 'id',
    menu varchar(2000) COMMENT '菜单json数据',
    app_id varchar(100) COMMENT 'AppID',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    primary key (id),
    unique key uk_app_id (app_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='公众号自定义菜单';

create table sys_ureport_data (
    id bigint NOT NULL COMMENT 'id',
    file_name varchar(200) COMMENT '报表文件名',
    content mediumblob COMMENT '内容',
    create_date datetime COMMENT '创建时间',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='报表数据';


INSERT INTO sys_user(id, username, password, real_name, head_url, gender, email, mobile, dept_id, super_admin, status, remark, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000001, 'admin', '$2a$10$012Kx2ba5jzqr9gLlG4MX.bnQJTD9UWqF57XDo2N3.fPtLne02u/m', '超级管理员', NULL, 1, 'root@renren.io', '13512345678', NULL, 1, 1, NULL, 0, NULL, now(), NULL, now());

INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000002, 0, '', 0, 0, 'icon-lock', '', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000003, 1067246875800000002, 'sys/user', 0, 0, 'icon-user', '', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000004, 1067246875800000003, '', 0, 1, '', 'sys:user:page,sys:user:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000005, 1067246875800000003, '', 0, 1, '', 'sys:user:save,sys:dept:list,sys:role:list', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000006, 1067246875800000003, '', 0, 1, '', 'sys:user:update,sys:dept:list,sys:role:list', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000007, 1067246875800000003, '', 0, 1, '', 'sys:user:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000008, 1067246875800000003, '', 0, 1, '', 'sys:user:export', 4, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000009, 1067246875800000002, 'sys/dept', 0, 0, 'icon-apartment', '', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000010, 1067246875800000009, '', 0, 1, '', 'sys:dept:list,sys:dept:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000011, 1067246875800000009, '', 0, 1, '', 'sys:dept:save', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000012, 1067246875800000009, '', 0, 1, '', 'sys:dept:update', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000013, 1067246875800000009, '', 0, 1, '', 'sys:dept:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000014, 1067246875800000002, 'sys/role', 0, 0, 'icon-team', '', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000015, 1067246875800000014, '', 0, 1, '', 'sys:role:page,sys:role:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000016, 1067246875800000014, '', 0, 1, '', 'sys:role:save,sys:menu:select,sys:dept:list', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000017, 1067246875800000014, '', 0, 1, '', 'sys:role:update,sys:menu:select,sys:dept:list', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000018, 1067246875800000014, '', 0, 1, '', 'sys:role:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000019, 0, '', 0, 0, 'icon-setting', NULL, 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000020, 1067246875800000019, 'sys/menu', 0, 0, 'icon-unorderedlist', NULL, 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000021, 1067246875800000020, NULL, 0, 1, NULL, 'sys:menu:list,sys:menu:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000022, 1067246875800000020, NULL, 0, 1, NULL, 'sys:menu:save', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000023, 1067246875800000020, NULL, 0, 1, NULL, 'sys:menu:update', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000024, 1067246875800000020, NULL, 0, 1, NULL, 'sys:menu:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000025, 1067246875800000019, 'sys/params', 0, 0, 'icon-fileprotect', '', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000026, 1067246875800000025, NULL, 0, 1, NULL, 'sys:params:page,sys:params:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000027, 1067246875800000025, NULL, 0, 1, NULL, 'sys:params:save', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000028, 1067246875800000025, NULL, 0, 1, NULL, 'sys:params:update', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000029, 1067246875800000025, NULL, 0, 1, NULL, 'sys:params:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000030, 1067246875800000025, '', 0, 1, NULL, 'sys:params:export', 4, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000031, 1067246875800000019, 'sys/dict-type', 0, 0, 'icon-gold', '', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000032, 1067246875800000031, '', 0, 1, '', 'sys:dict:page,sys:dict:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000033, 1067246875800000031, '', 0, 1, '', 'sys:dict:save', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000034, 1067246875800000031, '', 0, 1, '', 'sys:dict:update', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000035, 1067246875800000031, '', 0, 1, '', 'sys:dict:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000036, 0, '', 0, 0, 'icon-container', '', 4, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000037, 1067246875800000036, 'sys/log-login', 0, 0, 'icon-filedone', 'sys:log:login', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000038, 1067246875800000036, 'sys/log-operation', 0, 0, 'icon-solution', 'sys:log:operation', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000039, 1067246875800000036, 'sys/log-error', 0, 0, 'icon-file-exception', 'sys:log:error', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000040, 0, '', 0, 0, 'icon-desktop', '', 5, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000041, 1067246875800000040, '{{ApiUrl}}/monitor', 0, 0, 'icon-medicinebox', '', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000042, 1067246875800000040, '{{ApiUrl}}/doc.html', 1, 0, 'icon-file-word', '', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1164489061834969089, 1067246875800000019, 'sys/region', 0, 0, 'icon-location', '0', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1164492214366130178, 1164489061834969089, '', 0, 1, '', 'sys:region:list,sys:region:info', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1164492872829915138, 1164489061834969089, '', 0, 1, '', 'sys:region:save', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1164493252347318273, 1164489061834969089, '', 0, 1, '', 'sys:region:update', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1164493391254278145, 1164489061834969089, '', 0, 1, '', 'sys:region:delete', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1176372255559024642, 0, '', 0, 0, 'icon-windows', '', 999, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1206460008292216834, 1176372255559024642, 'sys/news', 0, 0, 'icon-file-word', 'demo:news:all', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000401, 0, '', 0, 0, 'icon-bell', '', 4, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000402, 1067246875800000401, 'sys/notice', 0, 0, 'icon-bell', 'sys:notice:all,sys:dept:list', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000403, 1067246875800000401, 'sys/notice-user', 0, 0, 'icon-notification', 'sys:notice:all', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1270380959719501825, 1176372255559024642, 'sys/product', 0, NULL, 0, 'icon-tag', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1270380959719501826, 1270380959719501825, NULL, 0, 'demo:product:page,demo:product:info', 1, NULL, 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1270380959719501827, 1270380959719501825, NULL, 0, 'demo:product:save', 1, NULL, 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1270380959719501828, 1270380959719501825, NULL, 0, 'demo:product:update', 1, NULL, 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1270380959719501829, 1270380959719501825, NULL, 0, 'demo:product:delete', 1, NULL, 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1300278047072649217, 0, '', 0, '', 0, 'icon-filesearch', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1300278435729440769, 1300278047072649217, '{{ApiUrl}}/sys/ureport/designer', 0, '', 0, 'icon-book', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1300381796852060161, 1300278047072649217, 'sys/ureport', 0, 'sys:ureport:all', 0, 'icon-up-circle', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1341596622612987906, 1067246875800000002, 'sys/post', 0, NULL, 0, 'icon-pic-left', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1341596622688485377, 1341596622612987906, NULL, 0, 'sys:post:page,sys:post:info', 1, NULL, 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1341596622755594242, 1341596622612987906, NULL, 0, 'sys:post:save', 1, NULL, 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1341596622835286018, 1341596622612987906, NULL, 0, 'sys:post:update', 1, NULL, 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1341596622902394881, 1341596622612987906, NULL, 0, 'sys:post:delete', 1, NULL, 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1341676084016852994, 1176372255559024642, 'sys/excel', 0, 'demo:excel:all', 0, 'icon-table', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1343074487777677313, 0, '', 0, '', 0, 'icon-Dollar', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1343074685589442561, 1343074487777677313, 'pay/order', 0, 'pay:order:all', 0, 'icon-unorderedlist', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1343074794440019970, 1343074487777677313, 'pay/alipaynotifylog', 0, 'pay:alipayNotifyLog:all', 0, 'icon-filedone', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1343074794440019971, 1343074487777677313, 'pay/wechatnotifylog', 0, 'pay:wechatNotifyLog:all', 0, 'icon-filedone', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1270380959719501800, 1176372255559024642, 'sys/charts', 0, '', 0, 'icon-tag', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu (id, pid, url, permissions, menu_type, open_style, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1419551957005963266, 0, '', '', 0, 0, 'icon-wechat-fill', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu (id, pid, url, permissions, menu_type, open_style, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1419553543706972161, 1419551957005963266, 'mp/account', 'mp:account:all', 0, 0, 'icon-user', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu (id, pid, url, permissions, menu_type, open_style, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1419963799817691137, 1419551957005963266, 'mp/menu', 'mp:menu:all', 0, 0, 'icon-unorderedlist', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());


INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501825, 'name', 'Master And Child', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501825, 'name', '主子表演示', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501825, 'name', '主子表演示', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501826, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501826, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501826, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501827, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501827, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501827, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501828, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501828, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501828, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501829, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501829, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501829, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300278047072649217, 'name', 'Report Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300278047072649217, 'name', '报表管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300278047072649217, 'name', '報表管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300278435729440769, 'name', 'Report Design', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300278435729440769, 'name', '报表设计器', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300278435729440769, 'name', '報表設計器', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300381796852060161, 'name', 'Report List', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300381796852060161, 'name', '报表列表', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1300381796852060161, 'name', '報表列表', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000002, 'name', '权限管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000002, 'name', '權限管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000002, 'name', 'Authority Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000003, 'name', '用户管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000003, 'name', '用戶管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000003, 'name', 'User Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000004, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000004, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000004, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000005, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000005, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000005, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000006, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000006, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000006, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000007, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000007, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000007, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000008, 'name', 'Export', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000008, 'name', '导出', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000008, 'name', '導出', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000009, 'name', 'Department Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000009, 'name', '部门管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000009, 'name', '部門管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000010, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000010, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000010, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000011, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000011, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000011, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000012, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000012, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000012, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000013, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000013, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000013, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000014, 'name', 'Role Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000014, 'name', '角色管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000014, 'name', '角色管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000015, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000015, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000015, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000016, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000016, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000016, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000017, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000017, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000017, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000018, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000018, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000018, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000019, 'name', 'Setting', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000019, 'name', '系统设置', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000019, 'name', '系統設置', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000020, 'name', 'Menu Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000020, 'name', '菜单管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000020, 'name', '菜單管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000021, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000021, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000021, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000022, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000022, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000022, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000023, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000023, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000023, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000024, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000024, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000024, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000025, 'name', 'Parameter Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000025, 'name', '参数管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000025, 'name', '參數管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000026, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000026, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000026, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000027, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000027, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000027, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000028, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000028, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000028, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000029, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000029, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000029, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000030, 'name', 'Export', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000030, 'name', '导出', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000030, 'name', '導出', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000031, 'name', 'Dict Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000031, 'name', '字典管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000031, 'name', '字典管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000032, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000032, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000032, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000033, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000033, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000033, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000034, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000034, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000034, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000035, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000035, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000035, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000036, 'name', 'Log Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000036, 'name', '日志管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000036, 'name', '日誌管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000037, 'name', 'Login Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000037, 'name', '登录日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000037, 'name', '登錄日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000038, 'name', 'Operation Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000038, 'name', '操作日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000038, 'name', '操作日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000039, 'name', 'Error Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000039, 'name', '异常日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000039, 'name', '異常日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000040, 'name', 'System Monitoring', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000040, 'name', '系统监控', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000040, 'name', '系統監控', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000041, 'name', 'Service Monitoring', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000041, 'name', '服务监控', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000041, 'name', '服務監控', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000042, 'name', 'Swagger Api', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000042, 'name', '接口文档', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000042, 'name', '接口文檔', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164489061834969089, 'name', 'Administrative Regions', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164489061834969089, 'name', '行政区域', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164489061834969089, 'name', '行政區域', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164492214366130178, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164492214366130178, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164492214366130178, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164492872829915138, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164492872829915138, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164492872829915138, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164493252347318273, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164493252347318273, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164493252347318273, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164493391254278145, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164493391254278145, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1164493391254278145, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176372255559024642, 'name', 'Demo', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176372255559024642, 'name', '功能示例', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176372255559024642, 'name', '功能示例', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1206460008292216834, 'name', 'News Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1206460008292216834, 'name', '新闻管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1206460008292216834, 'name', '新聞管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000401, 'name', 'Station Notice', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000401, 'name', '站内通知', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000401, 'name', '站內通知', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000402, 'name', 'Notice Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000402, 'name', '通知管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000402, 'name', '通知管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000403, 'name', 'My Notice', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000403, 'name', '我的通知', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000403, 'name', '我的通知', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501800, 'name', 'ECharts', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501800, 'name', 'ECharts', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501800, 'name', 'ECharts', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341676084016852994, 'name', 'Excel Demo', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341676084016852994, 'name', 'Excel导入演示', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341676084016852994, 'name', 'Excel導入演示', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074487777677313, 'name', 'Pay Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074487777677313, 'name', '支付管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074487777677313, 'name', '支付管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074685589442561, 'name', 'Order Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074685589442561, 'name', '订单管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074685589442561, 'name', '訂單管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074794440019970, 'name', 'AliPay Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074794440019970, 'name', '支付宝回调日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074794440019970, 'name', '支付寶回調日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074794440019971, 'name', 'WeChat Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074794440019971, 'name', '微信回调日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1343074794440019971, 'name', '微信回調日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622612987906, 'name', 'Job Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622612987906, 'name', '岗位管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622612987906, 'name', '崗位管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622688485377, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622688485377, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622688485377, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622755594242, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622755594242, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622755594242, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622835286018, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622835286018, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622835286018, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622902394881, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622902394881, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1341596622902394881, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419551957005963266, 'name', 'Wechat Management', 'en-US');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419551957005963266, 'name', '微信管理', 'zh-CN');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419551957005963266, 'name', '微信管理', 'zh-TW');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419553543706972161, 'name', 'Mp Management', 'en-US');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419553543706972161, 'name', '公众号管理', 'zh-CN');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419553543706972161, 'name', '公眾號管理', 'zh-TW');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419963799817691137, 'name', 'Custom Menu', 'en-US');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419963799817691137, 'name', '自定义菜单', 'zh-CN');
INSERT INTO sys_language (table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1419963799817691137, 'name', '自定義選單', 'zh-TW');


INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000061, 1067246875800000062, '1067246875800000065,1067246875800000062', '技术部', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000062, 1067246875800000065, '1067246875800000065', '长沙分公司', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000063, 1067246875800000065, '1067246875800000065', '上海分公司', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000064, 1067246875800000063, '1067246875800000065,1067246875800000063', '市场部', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000065, 0, '0', '人人开源集团', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000066, 1067246875800000063, '1067246875800000065,1067246875800000063', '销售部', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000067, 1067246875800000062, '1067246875800000065,1067246875800000062', '产品部', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_dict_type(id, dict_type, dict_name, remark, sort, creator, create_date, updater, update_date) VALUES (1160061077912858625, 'gender', '性别', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_type(id, dict_type, dict_name, remark, sort, creator, create_date, updater, update_date) VALUES (1225813644059140097, 'notice_type', '站内通知-类型', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_type(id, dict_type, dict_name, remark, sort, creator, create_date, updater, update_date) VALUES (1341593474355838978, 'post_status', '岗位管理状态', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_type(id, dict_type, dict_name, remark, sort, creator, create_date, updater, update_date) VALUES (1343069688596295682, 'order_status', '订单状态', '', 0, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1160061112075464705, 1160061077912858625, '男', '0', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1160061146967879681, 1160061077912858625, '女', '1', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1160061190127267841, 1160061077912858625, '保密', '2', '', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1225814069634195457, 1225813644059140097, '公告', '0', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1225814107559092225, 1225813644059140097, '会议', '1', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1225814271879340034, 1225813644059140097, '其他', '2', '', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1341593562419445762, 1341593474355838978, '停用', '0', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1341593595407646722, 1341593474355838978, '正常', '1', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1343069765549191170, 1343069688596295682, '已取消', '-1', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1343069839847092226, 1343069688596295682, '等待付款', '0', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1343069914518286337, 1343069688596295682, '已完成', '1', '', 2, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO tb_order(id, order_id, product_id, product_name, pay_amount, status, user_id, pay_at, create_date) VALUES (1343491774781419523, 1343491774781419523, 1, '人人企业版', 3600.00, 1, 1067246875800000001, now(), now());
INSERT INTO tb_order(id, order_id, product_id, product_name, pay_amount, status, user_id, pay_at, create_date) VALUES (1343491827268939779, 1343491827268939778, 2, '人人微服务版', 4800.00, 0, 1067246875800000001, NULL, now());
INSERT INTO tb_alipay_notify_log(id, out_trade_no, total_amount, buyer_pay_amount, receipt_amount, invoice_amount, notify_id, buyer_id, seller_id, trade_no, trade_status, create_date) VALUES (1343493644518195201, 1343491774781419523, 3600.00, 3600.00, 3600.00, 3600.00, '2020122800222174658006930510128003', '2088102177806934', '2088102177441441', '2020122822001406930501194003', 'TRADE_SUCCESS', now());

INSERT INTO sys_post(id, post_code, post_name, sort, status, creator, create_date, updater, update_date) VALUES (1341597192832811009, 'tech', '技术岗', 0, 1, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO tb_excel_data(id, real_name, user_identity, address, join_date, class_name, creator, create_date) VALUES (1343762012112445441, '大力', '430212199910102980', '上海市长宁区中山公园', now(), '姚班2101', 1067246875800000001, now());
