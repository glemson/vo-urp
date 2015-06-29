DROP TABLE IF EXISTS _MigrationScripts;
create table _MigrationScripts (
  createDate datetime default current_timestamp
, modelVersion varchar(128)
, migrateTables text
, dropTables text
, dropViews text);
