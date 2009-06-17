--
-- this file describes the tables required by web authentication	
--


-- default Parties :
INSERT INTO t_Party (ID, ivoId, name, address, email, telephone) VALUES 
  (1, 'ivo://pdrDB/party/1', 'Franck Le Petit', 'LUTh, Observatoire de Paris-Meudon - 5 place Jules Janssen - F-92 195 Meudon Cedex', 'Franck.LePetit@obspm.fr', '01 45 07 75 66'),
  (2, 'ivo://pdrDB/party/2', 'Laurent Bourges', 'LUTh, Observatoire de Paris-Meudon - 5 place Jules Janssen - F-92 195 Meudon Cedex', 'Laurent.Bourges@obspm.fr', '01 45 07 74 25');


CREATE TABLE web_users (
  user_name         VARCHAR(50) NOT NULL,
  user_pass         VARCHAR(15) NOT NULL,
  party_id           BIGINT NOT NULL
);

ALTER TABLE web_users ADD CONSTRAINT pk_web_users_ID PRIMARY KEY(user_name);

ALTER TABLE web_users ADD CONSTRAINT fk_web_users_party
    FOREIGN KEY (party_id) REFERENCES t_Party(ID);


INSERT INTO web_users (user_name, user_pass, party_id) VALUES 
  ('franck', 'franck', 1),
  ('laurent', 'laurent', 2);


create table web_user_roles (
  user_name         VARCHAR(50) NOT NULL,
  role_name         VARCHAR(15) NOT NULL
);

ALTER TABLE web_user_roles ADD CONSTRAINT pk_web_user_roles_ID PRIMARY KEY(user_name, role_name);

INSERT INTO web_user_roles (user_name, role_name) VALUES 
  ('franck', 'admin'),
  ('laurent', 'admin');
