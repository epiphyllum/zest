CREATE TABLE gen_datasource (
    id bigint NOT NULL,
    db_type varchar(200),
    conn_name varchar(200),
    conn_url varchar(500),
    username varchar(200),
    password varchar(200),
    status int,
    create_date datetime,
    primary key (id)
);


CREATE TABLE gen_field_type (
    id bigint NOT NULL,
    column_type varchar(200),
    attr_type varchar(200),
    package_name varchar(200),
    create_date datetime,
    primary key (id)
);

CREATE UNIQUE INDEX uk_gen_field_type_column_type on gen_field_type(column_type);


CREATE TABLE gen_base_class (
    id bigint NOT NULL,
    package_name varchar(200),
    code varchar(200),
    fields varchar(200),
    remark varchar(200),
    create_date datetime,
    primary key (id)
);


CREATE TABLE gen_table_info (
    id bigint NOT NULL,
    table_name varchar(200),
    class_name varchar(200),
    table_comment varchar(200),
    author varchar(200),
    email varchar(200),
    package_name varchar(200),
    version varchar(200),
    backend_url varchar(500),
    backend_path varchar(500),
    frontend_path varchar(500),
    module_name varchar(200),
    sub_module_name varchar(200),
    datasource_id bigint,
    baseclass_id bigint,
    create_date datetime,
    primary key (id)
);

CREATE UNIQUE INDEX uk_gen_table_info_table_name on gen_table_info(table_name);


CREATE TABLE gen_table_field (
    id bigint NOT NULL,
    table_id bigint,
    table_name varchar(200),
    column_name varchar(200),
    column_type varchar(200),
    column_comment varchar(200),
    attr_name varchar(200),
    attr_type varchar(200),
    package_name varchar(200),
    is_pk int,
    is_required int,
    is_form int,
    is_list int,
    is_query int,
    query_type varchar(200),
    form_type varchar(200),
    dict_name varchar(200),
    validator_type varchar(200),
    sort int,
    primary key (id)
);

CREATE INDEX idx_gen_table_field_table_name on gen_table_field(table_name);


CREATE TABLE gen_test_data (
    id bigint NOT NULL,
    username varchar(50),
    real_name varchar(50),
    gender int,
    email varchar(100),
    notice_type int,
    content text,
    creator bigint,
    create_date datetime,
    updater bigint,
    update_date datetime,
    PRIMARY KEY (id)
);



INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1302850622416084993, 0, '', 0, '', 0, 'icon-rocket', 3, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1302850783288614913, 1305532398162145281, 'devtools/datasource', 0, '', 0, 'icon-sync', 0, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1302862890696564738, 1305532398162145281, 'devtools/fieldtype', 0, '', 0, 'icon-eye', 1, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1302874751835848705, 1305532398162145281, 'devtools/baseclass', 0, '', 0, 'icon-info-circle', 3, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1304802103569809410, 1302850622416084993, 'devtools/generator', 0, '', 0, 'icon-tags', 0, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1305513187675144193, 1305532398162145281, 'devtools/param', 0, '', 0, 'icon-setting', 5, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1305532398162145281, 1302850622416084993, '', 0, '', 0, 'icon-setting', 3, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302850622416084993, 'name', 'DevTools', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302850622416084993, 'name', '开发者工具', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302850622416084993, 'name', '開發者工具', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302850783288614913, 'name', 'DataSource', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302850783288614913, 'name', '数据源管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302850783288614913, 'name', '數據源管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302862890696564738, 'name', 'Field Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302862890696564738, 'name', '字段管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302862890696564738, 'name', '字段管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302874751835848705, 'name', 'BaseClass', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302874751835848705, 'name', '基类管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1302874751835848705, 'name', '基類管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1304802103569809410, 'name', 'Code Generation', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1304802103569809410, 'name', '代码生成工具', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1304802103569809410, 'name', '代碼生成工具', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1305513187675144193, 'name', 'Parameter Config', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1305513187675144193, 'name', '参数配置', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1305513187675144193, 'name', '參數配置', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1305532398162145281, 'name', 'Config Info', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1305532398162145281, 'name', '配置信息', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1305532398162145281, 'name', '配置信息', 'zh-TW');

INSERT INTO sys_params(id, param_code, param_value, param_type, remark, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000072, 'DEV_TOOLS_PARAM_KEY', '{"packageName":"io.renren","version":"3.0","author":"Mark","email":"sunlightcs@gmail.com","backendPath":"D:\\renrenio\\renren-cloud-tenant\\renren-admin","frontendPath":"D:\\renrenio\\renren-cloud-tenant-admin"}', 0, '代码生成器配置信息', 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());

INSERT INTO gen_base_class(id, package_name, code, fields, remark, create_date) VALUES (1302875019642159105, '${package}.commons.mybatis.entity.BaseEntity', 'BaseEntity', 'id,creator,create_date', '专业版', getdate());
INSERT INTO gen_datasource(id, db_type, conn_name, conn_url, username, password, status, create_date) VALUES (1302855887882412034, 'MySQL', '本地', 'jdbc:mysql://localhost:3306/renren_cloud?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true', 'renren', '123456', 0, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152452777352425473, 'datetime', 'Date', 'java.util.Date', getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453412995002369, 'date', 'Date', 'java.util.Date', getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453603525455873, 'tinyint', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453660052090881, 'smallint', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453722136178689, 'mediumint', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453808874385409, 'int', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453849735294977, 'integer', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453895029583873, 'bigint', 'Long', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453931373228033, 'float', 'Float', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152453967880450050, 'double', 'Double', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454047601586177, 'decimal', 'BigDecimal', 'java.math.BigDecimal', getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454090760974338, 'bit', 'Boolean', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454147010785282, 'char', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454183136325633, 'varchar', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454312664821761, 'tinytext', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454343820111874, 'text', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454372077137921, 'mediumtext', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454401378545665, 'longtext', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454486267064322, 'timestamp', 'Date', 'java.util.Date', getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454630295269378, 'NUMBER', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454715645161474, 'BINARY_INTEGER', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454778828156930, 'BINARY_FLOAT', 'Float', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454828987838466, 'BINARY_DOUBLE', 'Double', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454885745160193, 'VARCHAR2', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454919756771329, 'NVARCHAR', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454952568811521, 'NVARCHAR2', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152454986349735938, 'CLOB', 'String', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152455109695827970, 'int8', 'Long', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152455153002016770, 'int4', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152455184669011969, 'int2', 'Integer', NULL, getdate());
INSERT INTO gen_field_type(id, column_type, attr_type, package_name, create_date) VALUES (1152455217359417345, 'numeric', 'BigDecimal', 'java.math.BigDecimal', getdate());

INSERT INTO gen_table_info(id, table_name, class_name, table_comment, author, email, package_name, version, backend_url, backend_path, frontend_path, module_name, sub_module_name, datasource_id, baseclass_id, create_date) VALUES (1308327671447859201, 'gen_test_data', 'TestData', '测试功能', 'Mark', 'sunlightcs@gmail.com', 'io.renren', '3.0', 'sys', 'D:\\renrenio\\renren-cloud-tenant\\renren-admin', 'D:\\renrenio\\renren-cloud-tenant-admin', 'gen', NULL, 0, 1302875019642159105, getdate());

INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671502385153, 1308327671447859201, 'gen_test_data', 'id', 'BIGINT', 'id', 'id', 'Long', NULL, 1, 0, 0, 0, 0, '=', 'text', NULL, NULL, 0);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671510773761, 1308327671447859201, 'gen_test_data', 'username', 'VARCHAR', '用户名', 'username', 'String', NULL, 0, 1, 1, 1, 1, 'like', 'text', NULL, NULL, 1);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671510773762, 1308327671447859201, 'gen_test_data', 'real_name', 'VARCHAR', '姓名', 'realName', 'String', NULL, 0, 1, 1, 1, 0, 'like', 'text', NULL, NULL, 2);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671510773763, 1308327671447859201, 'gen_test_data', 'gender', 'TINYINT', '性别', 'gender', 'Integer', NULL, 0, 1, 1, 1, 1, '=', 'select', 'gender', NULL, 3);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671510773764, 1308327671447859201, 'gen_test_data', 'email', 'VARCHAR', '邮箱', 'email', 'String', NULL, 0, 1, 1, 1, 0, '=', 'text', NULL, NULL, 4);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671514968065, 1308327671447859201, 'gen_test_data', 'notice_type', 'TINYINT', '类型', 'noticeType', 'Integer', NULL, 0, 1, 1, 1, 0, '=', 'radio', 'notice_type', NULL, 5);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671514968066, 1308327671447859201, 'gen_test_data', 'content', 'TEXT', '内容', 'content', 'String', NULL, 0, 1, 1, 0, 0, '=', 'editor', NULL, NULL, 6);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671514968067, 1308327671447859201, 'gen_test_data', 'creator', 'BIGINT', '创建者', 'creator', 'Long', NULL, 0, 0, 0, 0, 0, '=', 'text', NULL, NULL, 7);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671514968068, 1308327671447859201, 'gen_test_data', 'create_date', 'DATETIME', '创建时间', 'createDate', 'Date', 'java.util.Date', 0, 0, 0, 1, 1, '=', 'date', NULL, NULL, 8);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671514968069, 1308327671447859201, 'gen_test_data', 'updater', 'BIGINT', '更新者', 'updater', 'Long', NULL, 0, 0, 0, 0, 0, '=', 'text', NULL, NULL, 9);
INSERT INTO gen_table_field(id, table_id, table_name, column_name, column_type, column_comment, attr_name, attr_type, package_name, is_pk, is_required, is_form, is_list, is_query, query_type, form_type, dict_name, validator_type, sort) VALUES (1308327671523356674, 1308327671447859201, 'gen_test_data', 'update_date', 'DATETIME', '更新时间', 'updateDate', 'Date', 'java.util.Date', 0, 0, 0, 0, 0, '=', 'text', NULL, NULL, 10);


INSERT INTO gen_test_data(id, username, real_name, gender, email, notice_type, content, creator, create_date, updater, update_date) VALUES (1067246875800000001, 'sunlightcs', 'Mark', 0, 'root@renren.io', 0, '<p>人人开源代码生成器！</p>', 1067246875800000001, getdate(), 1067246875800000001, getdate());

INSERT INTO sys_menu(id, pid, url, permissions, menu_type, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1340949288542347266, 1302850622416084993, 'form-generator/form', '', 0, 'icon-edit-square', 0, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1340949288542347266, 'name', 'Page Design', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1340949288542347266, 'name', '页面表单设计', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1340949288542347266, 'name', '頁面表單設計', 'zh-TW');

INSERT INTO sys_menu (id, pid, url, permissions, menu_type, open_style, icon, sort, del_flag, creator, create_date, updater, update_date) VALUES (1340949288542347268, 1302850622416084993, 'http://datav.renren.io', '', 0, 1, 'icon-select', 0, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1340949288542347268, 'name', 'Large Screen', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1340949288542347268, 'name', '大屏设计', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1340949288542347268, 'name', '大屏設計', 'zh-TW');
