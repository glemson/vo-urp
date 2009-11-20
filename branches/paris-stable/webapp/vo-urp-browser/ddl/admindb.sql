create table users (username varchar(40) not null, password varchar(10) not null, createDate datetime default current_timestamp);

create table roles (rolename varchar(20) not null);

create table user_roles (username varchar(40) not null, rolename varchar(20) not null);

-- data :
delete from roles
insert into roles values('SimDB-member');

delete from users
insert into users values('admin', 'simdb');

delete from user_roles
insert into user_roles values('admin', 'SimDB-member');
