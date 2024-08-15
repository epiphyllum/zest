-- SEATA AT 模式需要 undo_log 表
CREATE TABLE undo_log
(
    id            NUMBER(19)    NOT NULL,
    branch_id     NUMBER(19)    NOT NULL,
    xid           VARCHAR2(100) NOT NULL,
    context       VARCHAR2(128) NOT NULL,
    rollback_info BLOB          NOT NULL,
    log_status    NUMBER(10)    NOT NULL,
    log_created   TIMESTAMP(0)  NOT NULL,
    log_modified  TIMESTAMP(0)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
);

COMMENT ON TABLE undo_log IS 'AT transaction mode undo table';

-- Generate ID using sequence and trigger
CREATE SEQUENCE UNDO_LOG_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE seata_storage (
   id  NUMBER(20)    NOT NULL,
   commodity_code VARCHAR2(255),
   total NUMBER(10),
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uk_seata_storage on seata_storage(commodity_code);

INSERT INTO seata_storage(id, commodity_code, total) VALUES (1, '1001', 99);