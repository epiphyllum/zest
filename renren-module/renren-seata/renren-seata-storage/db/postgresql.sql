-- SEATA AT 模式需要 undo_log 表
CREATE TABLE IF NOT EXISTS public.undo_log
(
    id            SERIAL       NOT NULL,
    branch_id     BIGINT       NOT NULL,
    xid           VARCHAR(100) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info BYTEA        NOT NULL,
    log_status    INT          NOT NULL,
    log_created   TIMESTAMP(0) NOT NULL,
    log_modified  TIMESTAMP(0) NOT NULL,
    CONSTRAINT pk_undo_log PRIMARY KEY (id),
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
);

CREATE TABLE seata_storage (
   id  BIGINT    NOT NULL,
   commodity_code VARCHAR(255),
   total INT,
   PRIMARY KEY (id),
   UNIQUE (commodity_code)
);

INSERT INTO seata_storage(id, commodity_code, total) VALUES (1, '1001', 99);