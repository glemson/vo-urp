USE master;
GO
IF DB_ID (N'jobdb') IS NOT NULL
DROP DATABASE jobdb;
GO
-- execute the CREATE DATABASE statement 
CREATE DATABASE jobdb
ON 
( NAME = Sales_dat,
    FILENAME = 'E:\SQLServerDB\sql_db\jobdb.mdf',
    SIZE = 10MB,
    MAXSIZE = UNLIMITED,
    FILEGROWTH = 10% )
LOG ON
( NAME = Sales_log,
    FILENAME = 'E:\SQLServerDB\sql_db\jobdb.ldf',
    SIZE = 5MB,
    MAXSIZE = UNLIMITED,
    FILEGROWTH = 10%);
GO


DROP login jobdb_user;
DROP login jobdb_admin;
DROP login jobdb_super;

CREATE login jobdb_user  WITH PASSWORD ='user', check_policy=OFF;
CREATE login jobdb_admin WITH PASSWORD ='admin', check_policy=OFF;
CREATE login jobdb_super WITH PASSWORD ='super', check_policy=OFF;

use jobdb;
exec sp_grantdbaccess 'jobdb_super'
exec sp_addrolemember @rolename='db_datareader',@membername='jobdb_super'
exec sp_addrolemember @rolename='db_datawriter',@membername='jobdb_super'
exec sp_addrolemember @rolename='db_owner',@membername='jobdb_super'
exec sp_addrolemember @rolename='db_ddladmin',@membername='jobdb_super'


exec sp_grantdbaccess 'jobdb_admin'
exec sp_addrolemember @rolename='db_datareader',@membername='jobdb_admin'
exec sp_addrolemember @rolename='db_datawriter',@membername='jobdb_admin'
exec sp_addrolemember @rolename='db_ddladmin',@membername='jobdb_admin'

exec sp_grantdbaccess 'jobdb_user'
exec sp_addrolemember @rolename='db_datareader',@membername='jobdb_user'
exec sp_addrolemember @rolename='db_datawriter',@membername='jobdb_user'

GO
