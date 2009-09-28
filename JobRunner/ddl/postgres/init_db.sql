-- defines jobdb group with users :

DROP ROLE jobdb_group;
CREATE ROLE jobdb_group WITH NOLOGIN;

ALTER ROLE jobdb_group SET client_encoding = 'UTF8';


DROP ROLE jobdb_user;
DROP ROLE jobdb_admin;
DROP ROLE jobdb_super;

CREATE ROLE jobdb_user  WITH LOGIN PASSWORD 'user' INHERIT IN ROLE jobdb_group;
CREATE ROLE jobdb_admin WITH LOGIN PASSWORD 'admin' INHERIT IN ROLE jobdb_group;
CREATE ROLE jobdb_super WITH SUPERUSER CREATEDB LOGIN PASSWORD 'super' INHERIT IN ROLE jobdb_group;


-- creates jobdb database owned by jobdb_super :

CREATE DATABASE jobdb WITH OWNER = jobdb_super;
