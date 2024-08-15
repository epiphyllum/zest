create table sys_sms
(
    id                   bigint NOT NULL,
    sms_code             varchar(32),
    platform             int,
    sms_config           varchar(2000),
    remark               varchar(200),
    creator              bigint,
    create_date          datetime,
    updater              bigint,
    update_date          datetime,
    primary key (id)
);
CREATE UNIQUE INDEX uk_sms_code on sys_sms(sms_code);
CREATE INDEX idx_sys_sms_create_date on sys_sms(create_date);


CREATE TABLE sys_sms_log (
    id bigint NOT NULL,
    sms_code   varchar(32),
    platform int,
    mobile varchar(20),
    params_1 varchar(50),
    params_2 varchar(50),
    params_3 varchar(50),
    params_4 varchar(50),
    status int,
    creator bigint,
    create_date datetime,
    PRIMARY KEY (id)
);
CREATE INDEX idx_sys_sms_log_sms_code on sys_sms_log(sms_code);


CREATE TABLE sys_mail_template (
  id bigint NOT NULL,
  name varchar(100),
  subject varchar(200),
  content text,
  creator bigint,
  create_date datetime,
  updater bigint,
  update_date datetime,
  PRIMARY KEY (id)
);

CREATE INDEX idx_mail_template_create_date on sys_mail_template(create_date);


CREATE TABLE sys_mail_log (
  id bigint NOT NULL,
  template_id bigint NOT NULL,
  mail_from varchar(200),
  mail_to varchar(400),
  mail_cc varchar(400),
  subject varchar(200),
  content text,
  status  int,
  creator bigint,
  create_date datetime,
  PRIMARY KEY (id)
);

CREATE INDEX idx_mail_log_create_date on sys_mail_log(create_date);

INSERT INTO sys_params(id, param_code, param_value, param_type, remark, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000145, 'MAIL_CONFIG_KEY', '{"smtp":"smtp.163.com","port":25,"username":"renrenio_test@163.com","password":"renren123456"}', 0, '邮件配置信息', 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());

INSERT INTO sys_mail_template(id, name, subject, content, create_date) VALUES (1067246875800000147, '验证码模板', '人人开源注册验证码', '<p>人人开源注册验证码：${code}</p>', getdate());

INSERT INTO sys_sms(id, sms_code, platform, sms_config, remark, creator, create_date, updater, update_date) VALUES (1228954061084676097, '1001', 1, '{"aliyunAccessKeyId":"1","aliyunAccessKeySecret":"1","aliyunSignName":"1","aliyunTemplateCode":"1","qcloudAppKey":"","qcloudSignName":"","qcloudTemplateId":"","qiniuAccessKey":"","qiniuSecretKey":"","qiniuTemplateId":""}', '', 1067246875800000001, getdate(), 1067246875800000001, getdate());

INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000056, 0, '', 0, 0, 'icon-message', '', 3, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000057, 1067246875800000056, 'message/sms', 0, 0, 'icon-layout', 'sys:sms:all', 0, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000058, 1067246875800000056, 'message/mail-template', 0, 0, 'icon-appstore', 'sys:mail:all', 1, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000059, 1067246875800000056, 'message/mail-log', 0, 0, 'icon-save', 'sys:mail:log', 2, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000301, 1067246875800000056, 'message/sms-log', 0, 0, 'icon-layout', 'sys:smslog:all', 1, 0, 1067246875800000001, getdate(), 1067246875800000001, getdate());


INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000056, 'name', 'Message Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000056, 'name', '消息管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000056, 'name', '消息管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000057, 'name', 'SMS Service', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000057, 'name', '短信服务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000057, 'name', '短信服務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000058, 'name', 'Mail Template', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000058, 'name', '邮件模板', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000058, 'name', '郵件模板', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000059, 'name', 'Mail Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000059, 'name', '邮件发送记录', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000059, 'name', '郵件發送記錄', 'zh-TW ');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000301, 'name', 'SMS History', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000301, 'name', '短信发送记录', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000301, 'name', '短信發送記錄', 'zh-TW');
