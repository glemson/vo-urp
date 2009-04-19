create table users (username varchar(20) not null, password varchar(20) not null)
create table roles (rolename varchar(20) not null)
create table user_roles (username varchar(20) not null, rolename varchar(20) not null) 

insert into roles values('SimDB-member')
insert into users values('*******', '********')
insert into user_roles values('*******', 'SimDB-member')
