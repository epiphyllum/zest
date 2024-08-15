CREATE TABLE schedule_job (
  id NUMBER(20, 0) NOT NULL,
  bean_name varchar2(200),
  params varchar2(2000),
  cron_expression varchar2(100),
  status NUMBER(2, 0),
  remark varchar2(255),
  creator NUMBER(20, 0),
  create_date date,
  updater NUMBER(20, 0),
  update_date date,
  PRIMARY KEY (id)
);

CREATE INDEX idx_schedule_job_create_date on schedule_job(create_date);

COMMENT ON TABLE schedule_job IS '定时任务';
COMMENT ON COLUMN schedule_job.id IS 'id';
COMMENT ON COLUMN schedule_job.bean_name IS 'spring bean名称';
COMMENT ON COLUMN schedule_job.params IS '参数';
COMMENT ON COLUMN schedule_job.cron_expression IS 'cron表达式';
COMMENT ON COLUMN schedule_job.status IS '任务状态  0：暂停  1：正常';
COMMENT ON COLUMN schedule_job.remark IS '备注';
COMMENT ON COLUMN schedule_job.creator IS '创建者';
COMMENT ON COLUMN schedule_job.create_date IS '创建时间';
COMMENT ON COLUMN schedule_job.updater IS '更新者';
COMMENT ON COLUMN schedule_job.update_date IS '更新时间';


CREATE TABLE schedule_job_log (
  id NUMBER(20, 0) NOT NULL,
  job_id NUMBER(20, 0) NOT NULL,
  bean_name varchar2(200),
  params varchar2(2000),
  status NUMBER(2, 0),
  error varchar2(2000),
  times NUMBER(10, 0),
  create_date date,
  PRIMARY KEY (id)
);

CREATE INDEX idx_job_log_job_id on schedule_job_log(job_id);
CREATE INDEX idx_job_log_create_date on schedule_job_log(create_date);

COMMENT ON TABLE schedule_job_log IS '定时任务日志';
COMMENT ON COLUMN schedule_job_log.id IS 'id';
COMMENT ON COLUMN schedule_job_log.bean_name IS 'spring bean名称';
COMMENT ON COLUMN schedule_job_log.params IS '参数';
COMMENT ON COLUMN schedule_job_log.status IS '任务状态  0：失败    1：成功';
COMMENT ON COLUMN schedule_job_log.error IS '失败信息';
COMMENT ON COLUMN schedule_job_log.times IS '耗时(单位：毫秒)';
COMMENT ON COLUMN schedule_job.create_date IS '创建时间';


INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000047, 1067246875800000019, 'job/schedule', 0, 0, 'icon-edit-square', '', 3, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000048, 1067246875800000047, '', 0, 1, '', 'sys:schedule:page,sys:schedule:info', 0, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000049, 1067246875800000047, '', 0, 1, '', 'sys:schedule:save', 1, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000050, 1067246875800000047, '', 0, 1, '', 'sys:schedule:update', 2, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000051, 1067246875800000047, '', 0, 1, '', 'sys:schedule:delete', 3, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000052, 1067246875800000047, '', 0, 1, '', 'sys:schedule:pause', 4, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000053, 1067246875800000047, '', 0, 1, '', 'sys:schedule:resume', 5, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000054, 1067246875800000047, '', 0, 1, '', 'sys:schedule:run', 6, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000055, 1067246875800000047, '', 0, 1, '', 'sys:schedule:log', 7, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);


INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000047, 'name', 'Timed Task', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000047, 'name', '定时任务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000047, 'name', '定時任務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000048, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000048, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000048, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000049, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000049, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000049, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000050, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000050, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000050, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000051, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000051, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000051, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000052, 'name', 'Pause', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000052, 'name', '暂停', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000052, 'name', '暫停', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000053, 'name', 'Resume', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000053, 'name', '恢复', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000053, 'name', '恢復', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000054, 'name', 'Execute', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000054, 'name', '立即执行', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000054, 'name', '立即執行', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000055, 'name', 'Log List', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000055, 'name', '日志列表', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000055, 'name', '日誌列表', 'zh-TW');

INSERT INTO schedule_job (id, bean_name, params, cron_expression, status, remark, creator, create_date, updater, update_date) VALUES (1067246875800000144, 'testTask', 'renren', '0 0/30 * * * ?', '1', '有参数测试', 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);


--  quartz自带表结构
CREATE TABLE qrtz_job_details
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  JOB_NAME  VARCHAR2(200) NOT NULL,
  JOB_GROUP VARCHAR2(200) NOT NULL,
  DESCRIPTION VARCHAR2(250) NULL,
  JOB_CLASS_NAME   VARCHAR2(250) NOT NULL,
  IS_DURABLE VARCHAR2(1) NOT NULL,
  IS_NONCONCURRENT VARCHAR2(1) NOT NULL,
  IS_UPDATE_DATA VARCHAR2(1) NOT NULL,
  REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
  JOB_DATA BLOB NULL,
  CONSTRAINT QRTZ_JOB_DETAILS_PK PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_triggers
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  TRIGGER_NAME VARCHAR2(200) NOT NULL,
  TRIGGER_GROUP VARCHAR2(200) NOT NULL,
  JOB_NAME  VARCHAR2(200) NOT NULL,
  JOB_GROUP VARCHAR2(200) NOT NULL,
  DESCRIPTION VARCHAR2(250) NULL,
  NEXT_FIRE_TIME NUMBER(13) NULL,
  PREV_FIRE_TIME NUMBER(13) NULL,
  PRIORITY NUMBER(13) NULL,
  TRIGGER_STATE VARCHAR2(16) NOT NULL,
  TRIGGER_TYPE VARCHAR2(8) NOT NULL,
  START_TIME NUMBER(13) NOT NULL,
  END_TIME NUMBER(13) NULL,
  CALENDAR_NAME VARCHAR2(200) NULL,
  MISFIRE_INSTR NUMBER(2) NULL,
  JOB_DATA BLOB NULL,
  CONSTRAINT QRTZ_TRIGGERS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  CONSTRAINT QRTZ_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
  REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_simple_triggers
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  TRIGGER_NAME VARCHAR2(200) NOT NULL,
  TRIGGER_GROUP VARCHAR2(200) NOT NULL,
  REPEAT_COUNT NUMBER(7) NOT NULL,
  REPEAT_INTERVAL NUMBER(12) NOT NULL,
  TIMES_TRIGGERED NUMBER(10) NOT NULL,
  CONSTRAINT QRTZ_SIMPLE_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  CONSTRAINT QRTZ_SIMPLE_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_cron_triggers
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  TRIGGER_NAME VARCHAR2(200) NOT NULL,
  TRIGGER_GROUP VARCHAR2(200) NOT NULL,
  CRON_EXPRESSION VARCHAR2(120) NOT NULL,
  TIME_ZONE_ID VARCHAR2(80),
  CONSTRAINT QRTZ_CRON_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  CONSTRAINT QRTZ_CRON_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_simprop_triggers
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  TRIGGER_NAME VARCHAR2(200) NOT NULL,
  TRIGGER_GROUP VARCHAR2(200) NOT NULL,
  STR_PROP_1 VARCHAR2(512) NULL,
  STR_PROP_2 VARCHAR2(512) NULL,
  STR_PROP_3 VARCHAR2(512) NULL,
  INT_PROP_1 NUMBER(10) NULL,
  INT_PROP_2 NUMBER(10) NULL,
  LONG_PROP_1 NUMBER(13) NULL,
  LONG_PROP_2 NUMBER(13) NULL,
  DEC_PROP_1 NUMERIC(13,4) NULL,
  DEC_PROP_2 NUMERIC(13,4) NULL,
  BOOL_PROP_1 VARCHAR2(1) NULL,
  BOOL_PROP_2 VARCHAR2(1) NULL,
  CONSTRAINT QRTZ_SIMPROP_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  CONSTRAINT QRTZ_SIMPROP_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_blob_triggers
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  TRIGGER_NAME VARCHAR2(200) NOT NULL,
  TRIGGER_GROUP VARCHAR2(200) NOT NULL,
  BLOB_DATA BLOB NULL,
  CONSTRAINT QRTZ_BLOB_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  CONSTRAINT QRTZ_BLOB_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_calendars
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  CALENDAR_NAME  VARCHAR2(200) NOT NULL,
  CALENDAR BLOB NOT NULL,
  CONSTRAINT QRTZ_CALENDARS_PK PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);
CREATE TABLE qrtz_paused_trigger_grps
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  TRIGGER_GROUP  VARCHAR2(200) NOT NULL,
  CONSTRAINT QRTZ_PAUSED_TRIG_GRPS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_fired_triggers
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  ENTRY_ID VARCHAR2(95) NOT NULL,
  TRIGGER_NAME VARCHAR2(200) NOT NULL,
  TRIGGER_GROUP VARCHAR2(200) NOT NULL,
  INSTANCE_NAME VARCHAR2(200) NOT NULL,
  FIRED_TIME NUMBER(13) NOT NULL,
  SCHED_TIME NUMBER(13) NOT NULL,
  PRIORITY NUMBER(13) NOT NULL,
  STATE VARCHAR2(16) NOT NULL,
  JOB_NAME VARCHAR2(200) NULL,
  JOB_GROUP VARCHAR2(200) NULL,
  IS_NONCONCURRENT VARCHAR2(1) NULL,
  REQUESTS_RECOVERY VARCHAR2(1) NULL,
  CONSTRAINT QRTZ_FIRED_TRIGGER_PK PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);
CREATE TABLE qrtz_scheduler_state
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  INSTANCE_NAME VARCHAR2(200) NOT NULL,
  LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
  CHECKIN_INTERVAL NUMBER(13) NOT NULL,
  CONSTRAINT QRTZ_SCHEDULER_STATE_PK PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);
CREATE TABLE qrtz_locks
(
  SCHED_NAME VARCHAR2(120) NOT NULL,
  LOCK_NAME  VARCHAR2(40) NOT NULL,
  CONSTRAINT QRTZ_LOCKS_PK PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

create index idx_qrtz_j_req_recovery on qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);


