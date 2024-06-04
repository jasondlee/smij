drop table if exists person;
drop table if exists useraccount;
drop sequence if exists person_seq;
drop sequence if exists useraccount_seq;

create table UserAccount
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
    creationDate timestamptz default now(),
    primary key (id)
);

create table PasswordRecovery
(
    id  bigint not null,
    userName varchar(255),
    recoveryToken varchar(6),
    expiryDate timestamptz
);

create sequence UserAccount_SEQ start with 1 increment by 1;
create sequence PasswordRecovery_SEQ start with 1 increment by 1;

insert into useraccount (id, userName, password) values (1, 'jason@steeplesoft.com', 'password');
insert into useraccount (id, userName, password) values (2, 'jason@theleehouse.net', 'bar');
insert into useraccount (id, userName, password) values (3, 'jason+test@theleehouse.net', 'baz');

ALTER SEQUENCE useraccount_seq RESTART WITH 4;