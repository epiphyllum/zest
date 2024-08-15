CREATE TABLE sys_oss (
  id int8 NOT NULL,
  url varchar(200),
  creator int8,
  create_date timestamp,
  PRIMARY KEY (id)
);
CREATE INDEX idx_sys_oss_create_date on sys_oss(create_date);

COMMENT ON TABLE sys_oss IS '文件上传';
COMMENT ON COLUMN sys_oss.id IS 'id';
COMMENT ON COLUMN sys_oss.url IS 'URL地址';
COMMENT ON COLUMN sys_oss.creator IS '创建者';
COMMENT ON COLUMN sys_oss.create_date IS '创建时间';


INSERT INTO sys_menu(id, pid, url, open_style, menu_type, icon, permissions, sort, del_flag, creator, create_date, updater, update_date) VALUES (1067246875800000060, 1067246875800000019, 'oss/oss', 0, 0, 'icon-upload', 'sys:oss:all', 4, 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000060, 'name', 'File Upload', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000060, 'name', '文件上传', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000060, 'name', '文件上傳', 'zh-TW');

INSERT INTO sys_params(id, param_code, param_value, param_type, remark, del_flag,  creator, create_date, updater, update_date) VALUES (1067246875800000167, 'CLOUD_STORAGE_CONFIG_KEY', '{"type":1,"qiniuDomain":"http://pgzjvphhb.bkt.clouddn.com","qiniuPrefix":"upload","qiniuAccessKey":"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ","qiniuSecretKey":"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV","qiniuBucketName":"renren-oss","aliyunDomain":"","aliyunPrefix":"","aliyunEndPoint":"","aliyunAccessKeyId":"","aliyunAccessKeySecret":"","aliyunBucketName":"","qcloudDomain":"","qcloudPrefix":"","qcloudSecretId":"","qcloudSecretKey":"","qcloudBucketName":""}', '0', '云存储配置信息', 0, 1067246875800000001, CURRENT_DATE, 1067246875800000001, CURRENT_DATE);
