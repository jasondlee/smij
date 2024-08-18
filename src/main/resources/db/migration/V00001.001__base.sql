create table if not exists user_account
(
    id           bigint not null,
    userName     varchar(255) unique,
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

create table if not exists password_recovery
(
    id  bigint not null,
    userName varchar(255),
    recoveryToken varchar(6),
    expiryDate timestamptz
);

create table if not exists jwt_metadata
(
    id  text not null,
    expiresAt bigint,
    revoked boolean
);

create or replace view ValidRecoveryToken as
    select * from password_recovery where expiryDate <= now();

create sequence if not exists user_account_seq start with 1 increment by 1;
create sequence if not exists password_recovery_seq start with 1 increment by 1;

delete from user_account where id < 0;
insert into user_account (id, userName, password, roles) values (-1, 'admin@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'ADMIN');
insert into user_account (id, userName, password, roles) values (-2, 'admin2@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'ADMIN,USER');
insert into user_account (id, userName, password, roles) values (-3, 'user@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'USER');
insert into user_account (id, userName, password, roles, lockedUntil) values (-4, 'locked@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'USER', '2199-12-31 23:59:29');
insert into user_account (id, userName, password, roles, lockedUntil) values (-5, 'locked2@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'USER', '2199-12-31 23:59:29');

--ALTER SEQUENCE user_account_seq RESTART WITH 5;