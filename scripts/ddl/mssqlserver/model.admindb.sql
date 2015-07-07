
if  not exists (select * from dbo.sysobjects
	where id = object_id(N'[_Models]'))
CREATE TABLE [dbo].[_Models](
	[created] [datetime] NULL DEFAULT (getdate()),
     modelCreated varchar(20) not null unique,
	[modelVersion] [varchar](128) NULL,
	[modelXML] [text] NULL,
	[createTables] [text] NULL,
	[createViews] [text] NULL,
	[dropTables] [text] NULL,
	[dropViews] [text] NULL,
	[backupTables] [text] NULL,
	[dropBackupTables] [text] NULL,
	[migrateTables] [text] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]


if  not exists (select * from dbo.sysobjects
	where id = object_id(N'[_CurrentModel]'))
create table dbo._CurrentModel (
    created datetime NULL DEFAULT (getdate()),
     modelCreated varchar(20),
)

