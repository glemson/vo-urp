-- last modification date of the script : 21 12 2009


-- ------------------------------------------------------------------------------
--  Table RunContext
-- ------------------------------------------------------------------------------
CREATE TABLE run_context (
  ID SERIAL8
, DTYPE  VARCHAR(32) 
, OPTLOCK INTEGER
, parentId BIGINT -- RootContext
, name VARCHAR(32) NOT NULL
, description TEXT
, creationDate TIMESTAMP
, queueDate TIMESTAMP
, runDate TIMESTAMP
, endDate TIMESTAMP
, duration BIGINT
, state VARCHAR(32) NOT NULL
);

ALTER TABLE run_context ADD CONSTRAINT pk_run_context_ID PRIMARY KEY(ID);

CREATE INDEX ix_run_context_parent ON run_context(parentId);


-- ------------------------------------------------------------------------------
--  Table RootContext
-- ------------------------------------------------------------------------------
CREATE TABLE root_context (
  ID BIGINT NOT NULL
, owner VARCHAR(32) NOT NULL
, workingDir VARCHAR(255) NOT NULL
, relativePath VARCHAR(255)
, currentTask INTEGER
);

ALTER TABLE root_context ADD CONSTRAINT pk_root_context_ID PRIMARY KEY(ID);

ALTER TABLE root_context ADD CONSTRAINT fk_root_context_extends
    FOREIGN KEY (ID) REFERENCES run_context(ID);


-- ------------------------------------------------------------------------------
--  Table ProcessContext
-- ------------------------------------------------------------------------------
CREATE TABLE process_context (
  ID BIGINT NOT NULL
, command VARCHAR(1024) NOT NULL
, status INTEGER
);

ALTER TABLE process_context ADD CONSTRAINT pk_process_context_ID PRIMARY KEY(ID);

ALTER TABLE process_context ADD CONSTRAINT fk_process_context_extends
    FOREIGN KEY (ID) REFERENCES run_context(ID);


-- ------------------------------------------------------------------------------
--  Table ParameterSetting
-- ------------------------------------------------------------------------------
CREATE TABLE parameter_setting (
  ID SERIAL8
, containerId bigint not null
, name VARCHAR(128) NOT NULL
, value varchar(1024) not null
);


ALTER TABLE parameter_setting ADD CONSTRAINT pk_parameter_setting_ID PRIMARY KEY(ID);

create index ix_parameter_setting_container on parameter_setting(containerId);


