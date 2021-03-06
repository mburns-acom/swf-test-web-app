

--create table user_authority (
--    authority varchar(255) not null,
--    primary key (authority)
--)
--
--create table user_details (
--    id bigint generated by default as identity (start with 1),
--    username varchar(255) not null,
--    password varchar(255) not null,
--    name varchar(255) not null,
--    enabled boolean not null,
--    administrator boolean not null,
--    create_date timestamp not null,
--    last_updated_date timestamp not null,
--    last_login_date timestamp,
--    number_of_visits integer not null,
--    primary key (id)
--)
--
--create table user_details_authorities (
--    user_details bigint not null,
--    authorities bigint not null
--)

INSERT INTO user_authority (authority) VALUES ('ROLE_USER');
INSERT INTO user_authority (authority) VALUES ('ROLE_ADMIN');

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'mmeany', 'password1', 'Mark', true, '2014-05-01 07:00:00', '2014-05-01 07:00:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'smeany', 'password1', 'Serena', true, '2014-06-11 08:22:00', '2014-06-11 08:45:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'jsmith', 'password1', 'John', false, '2014-11-21 08:35:00', '2014-11-21 15:12:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'rjones', 'password1', 'Rachel', true, '2014-08-09 10:11:00', '2014-08-09 07:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'hmattews', 'password1', 'Hailey', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);


INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'rmeany', 'password1', 'Robert', true, '2014-06-11 08:22:00', '2014-06-11 08:45:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'psmith', 'password1', 'Paul', false, '2014-11-21 08:35:00', '2014-11-21 15:12:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'kjones', 'password1', 'Karen', true, '2014-08-09 10:11:00', '2014-08-09 07:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'lmattews', 'password1', 'Louise', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);


INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb1', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb2', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb3', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb4', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb5', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb6', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb7', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb8', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb9', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb10', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);


INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb11', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb12', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb13', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb14', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb15', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb16', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb17', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb18', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb19', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details (id, version, username, password, name, enabled, create_date, last_updated_date, number_of_visits)
 VALUES (null, 1, 'thepleb20', 'password1', 'Pleb', true, '2014-04-15 12:00:00', '2014-04-19 11:30:00', 0);

INSERT INTO user_details_authorities (user_details, authorities) VALUES (1, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (2, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (3, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (4, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (5, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (6, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (7, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (8, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (9, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (10, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (12, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (13, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (14, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (15, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (16, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (17, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (18, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (19, 'ROLE_USER');
INSERT INTO user_details_authorities (user_details, authorities) VALUES (20, 'ROLE_USER');

