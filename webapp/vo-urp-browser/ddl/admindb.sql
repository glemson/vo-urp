create table users (username varchar(40) not null, password varchar(10) not null);

create table roles (rolename varchar(20) not null);

create table user_roles (username varchar(40) not null, rolename varchar(20) not null);

-- data :

insert into roles values('SimDB-member');

insert into users values('admin', 'simdb');

insert into user_roles values('admin', 'SimDB-member');
