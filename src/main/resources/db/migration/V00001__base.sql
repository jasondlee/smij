drop table if exists user_account;
drop sequence if exists user_account_seq;

create table user_account
(
    id           bigint not null,
    userName     varchar(255),
    password     varchar(255),
    phoneNumber  varchar(50),
    address1     varchar(100),
    address2     varchar(100),
    city         varchar(100),
    state        varchar(10),
    zipCode      varchar(10),
    roles        varchar(1000),
    creationDate timestamptz default now(),
    failAttempts int default 0,
    lockedUntil  timestamptz,
    primary key (id)
);

create table password_recovery
(
    id  bigint not null,
    userName varchar(255),
    recoveryToken varchar(6),
    expiryDate timestamptz
);

create table jwt
(
    id  bigint not null,
    token text
);

create or replace view ValidRecoveryToken as
    select * from password_recovery where expiryDate <= now();

create sequence user_account_seq start with 1 increment by 1;
create sequence password_recovery_seq start with 1 increment by 1;

insert into user_account (id, userName, password, roles) values (1, 'admin', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN');
insert into user_account (id, userName, password, roles) values (2, 'jason@steeplesoft.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN,USER');
insert into user_account (id, userName, password, roles) values (3, 'jason@theleehouse.net', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER');

ALTER SEQUENCE user_account_seq RESTART WITH 4;