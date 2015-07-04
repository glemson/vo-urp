if not exists (select * from dbo.sysobjects
	where id = object_id(N'[users]'))
  create table users (username varchar(128) not null, password varchar(128) not null, createDate datetime default current_timestamp);

if not exists (select * from dbo.sysobjects
	where id = object_id(N'[roles]'))
  create table roles (rolename varchar(128) not null);

if not exists (select * from dbo.sysobjects
	where id = object_id(N'[user_roles]'))
  create table user_roles (username varchar(128) not null, rolename varchar(128) not null);

