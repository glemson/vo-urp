-- last modification date of the script : 25 09 2009


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

