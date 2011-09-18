DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_roles;

create table users (username varchar(128) not null, password varchar(128) not null, createDate TIMESTAMP default current_timestamp);
create table roles (rolename varchar(128) not null);
create table user_roles (username varchar(128) not null, rolename varchar(128) not null);

-- data :
insert into roles (rolename)
 select '@webapp.member.role@' 
  where not exists (select 0 from roles where rolename= '@webapp.member.role@');

insert into roles (rolename)
 select '@webapp.admin.role@' 
  where not exists (select 0 from roles where rolename= '@webapp.admin.role@');

-- next should maybe check that user has proper password if it exists?
insert into users (username, password)
 select '@webapp.admin.user@', '@webapp.admin.password@' 
  where not exists (select 0 from users where username= '@webapp.admin.user@');

insert into user_roles (username, rolename) 
 select '@webapp.admin.user@', '@webapp.admin.role@' 
  where not exists (select 0 from user_roles where username= '@webapp.admin.user@' and rolename = '@webapp.admin.role@');

insert into user_roles (username, rolename) 
 select '@webapp.admin.user@', '@webapp.member.role@' 
  where not exists (select 0 from user_roles where username= '@webapp.admin.user@' and rolename = '@webapp.member.role@');

