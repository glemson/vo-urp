DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_roles;

create table users (username varchar(128) not null, password varchar(128) not null, createDate TIMESTAMP default current_timestamp);
create table roles (rolename varchar(128) not null);
create table user_roles (username varchar(128) not null, rolename varchar(128) not null);

