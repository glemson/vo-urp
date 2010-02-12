-- last modification date of the script : 21 12 2009


-- ------------------------------------------------------------------------------
--  Table RunContext
-- ------------------------------------------------------------------------------
CREATE TABLE run_context (
  ID BIGINT identity not null
, DTYPE  VARCHAR(32) 
, OPTLOCK INTEGER
, parentId BIGINT -- RootContext
, name VARCHAR(32) NOT NULL
, description TEXT
, creationDate DATETIME
, queueDate DATETIME
, runDate DATETIME
, endDate DATETIME
, duration BIGINT
, state VARCHAR(32) NOT NULL
);
GO
ALTER TABLE run_context ADD CONSTRAINT pk_run_context_ID PRIMARY KEY(ID);

CREATE INDEX ix_run_context_parent ON run_context(parentId);
GO

-- ------------------------------------------------------------------------------
--  Table RootContext
-- ------------------------------------------------------------------------------
CREATE TABLE root_context (
  ID BIGINT not null
, owner VARCHAR(32) NOT NULL
, workingDir VARCHAR(255) NOT NULL
, relativePath VARCHAR(255)
, currentTask INTEGER
);
GO

ALTER TABLE root_context ADD CONSTRAINT pk_root_context_ID PRIMARY KEY(ID);
GO

-- ------------------------------------------------------------------------------
--  Table ProcessContext
-- ------------------------------------------------------------------------------
CREATE TABLE process_context (
  ID BIGINT not null
, command VARCHAR(1024) NOT NULL
, status INTEGER
);
GO

ALTER TABLE process_context ADD CONSTRAINT pk_process_context_ID PRIMARY KEY(ID);
GO

-- ------------------------------------------------------------------------------
--  Table ParameterSetting
-- ------------------------------------------------------------------------------
CREATE TABLE parameter_setting (
  ID BIGINT identity not null
, containerId bigint not null
, name VARCHAR(128) NOT NULL
, value varchar(1024) not null
);
GO

ALTER TABLE parameter_setting ADD CONSTRAINT pk_parameter_setting_ID PRIMARY KEY(ID);

create index ix_parameter_setting_container on parameter_setting(containerId);
GO



-- ---------------------------------------------------
-- tables holding roles, users and user-role combinations.
-- ---------------------------------------------------
CREATE TABLE roles (
  rolename varchar(32) not null
)

create table users (
  userName varchar(32) not null,
  password varchar(32) not null,
  partyName varchar(64),
  email varchar(128)
)

create table user_roles (
  username varchar(32) not null,
  rolename varchar(32) not null
)

insert into roles values('JobRunner-member');
insert into roles values('JobRunner-sesam');
insert into roles values('JobRunner-smac');

