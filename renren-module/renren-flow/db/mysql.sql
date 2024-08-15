INSERT INTO sys_user(id, username, password, real_name, head_url, gender, email, mobile, dept_id, super_admin, status, remark, del_flag, creator, create_date, updater, update_date) VALUES (1353943117220315138, 'test', '$2a$10$012Kx2ba5jzqr9gLlG4MX.bnQJTD9UWqF57XDo2N3.fPtLne02u/m', '测试用户', NULL, 0, 'test@renren.io', '13012345678', NULL, 0, 1, NULL, 0, NULL, now(), NULL, now());

INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000043, 0, '', 0, 0, 'icon-cluster', '', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000044, 1067246875800000043, 'flow/bpmform', 0, 0, 'icon-detail', 'flow:bpmform:all', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000045, 1067246875800000043, 'flow/model', 0, 0, 'icon-appstore-fill', 'sys:model:all', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000046, 1067246875800000043, 'flow/running', 0, 0, 'icon-play-square', 'sys:running:all,sys:flow:all', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1176004047773671425, 1176004381581549569, 'flow/start', 0, 0, 'icon-play-square', 'sys:flow:all', 0, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1176004381581549569, 0, '', 0, 0, 'icon-user', '', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1176006162176843777, 1176004381581549569, 'flow/todo', 0, 0, 'icon-dashboard', 'sys:flow:all,sys:user:page', 1, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1176010201362898946, 1176004381581549569, 'flow/done', 0, 0, 'icon-check-square', 'sys:flow:all', 2, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1176009642778075138, 1176004381581549569, 'flow/my-send', 0, 0, 'icon-edit-square', 'sys:flow:all', 3, 0, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000043, 'name', 'Work Process', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000043, 'name', '工作流程', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000043, 'name', '工作流程', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000044, 'name', 'Form Design', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000044, 'name', '表单设计', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000044, 'name', '表单设计', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000045, 'name', 'Process Design', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000045, 'name', '流程设计', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000045, 'name', '流程设计', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000046, 'name', 'Running Process', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000046, 'name', '运行中的流程', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000046, 'name', '運行中的流程', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176004047773671425, 'name', 'Initiation Process', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176004047773671425, 'name', '发起流程', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176004047773671425, 'name', '發起流程', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176004381581549569, 'name', 'Office Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176004381581549569, 'name', '办公管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176004381581549569, 'name', '辦公管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176006162176843777, 'name', 'Todo', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176006162176843777, 'name', '待办任务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176006162176843777, 'name', '待辦任务', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176010201362898946, 'name', 'Task Already', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176010201362898946, 'name', '已办任务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176010201362898946, 'name', '已辦任務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176009642778075138, 'name', 'My Send', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176009642778075138, 'name', '我发起的', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1176009642778075138, 'name', '我發起的', 'zh-TW');

CREATE TABLE bpm_form (
    id bigint NOT NULL COMMENT 'id',
    name varchar(50) DEFAULT NULL COMMENT '名称',
    remark varchar(200) DEFAULT NULL COMMENT '备注',
    content text COMMENT '表单内容',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='流程表单';

CREATE TABLE bpm_definition_ext (
    id bigint NOT NULL COMMENT 'id',
    model_id varchar(64) COMMENT '模型ID',
    process_definition_id varchar(64) COMMENT '流程定义ID',
    form_type varchar(64) COMMENT '表单类型',
    form_id varchar(500) COMMENT '表单ID',
    form_content text COMMENT '表单内容',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='流程定义扩展';

CREATE TABLE bpm_instance_ext (
    id bigint NOT NULL COMMENT 'id',
    process_instance_id varchar(64) COMMENT '流程实例ID',
    process_definition_id varchar(64) COMMENT '流程定义ID',
    form_variables varchar(4000) COMMENT '表单值',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='流程实例扩展';

CREATE TABLE bpm_form_correction (
    id bigint(20) NOT NULL COMMENT 'id',
    apply_post varchar(255) COMMENT '申请岗位',
    entry_date datetime COMMENT '入职日期',
    correction_date datetime COMMENT '转正日期',
    work_content varchar(2000) COMMENT '工作内容',
    achievement varchar(2000) COMMENT '工作成绩',
    instance_id varchar(80) COMMENT '实例ID',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='自定义表单(转正申请)';