-- defines simdb group with users :

DROP ROLE simdb_group;
CREATE ROLE simdb_group WITH NOLOGIN;

ALTER ROLE simdb_group SET client_encoding = 'UTF8';


DROP ROLE simdb_user;
DROP ROLE simdb_admin;
DROP ROLE simdb_super;

CREATE ROLE simdb_user  WITH LOGIN PASSWORD 'user' INHERIT IN ROLE simdb_group;
CREATE ROLE simdb_admin WITH LOGIN PASSWORD 'admin' INHERIT IN ROLE simdb_group;
CREATE ROLE simdb_super WITH SUPERUSER CREATEDB LOGIN PASSWORD 'super' INHERIT IN ROLE simdb_group;


-- creates simdb database owned by simdb_super :

CREATE DATABASE simdb WITH OWNER = simdb_super;
