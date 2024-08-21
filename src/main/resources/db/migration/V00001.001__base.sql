-- noinspection SqlNoDataSourceInspectionForFile
create sequence if not exists user_account_seq start with 1 increment by 1;
create sequence if not exists password_recovery_seq start with 1 increment by 1;

create table if not exists user_account
(
    id            bigint DEFAULT nextval('user_account_seq') PRIMARY KEY,
    user_name     varchar(255) unique,
    password      varchar(255),
    phone_number  varchar(50),
    address1      varchar(100),
    address2      varchar(100),
    city          varchar(100),
    state         varchar(10),
    zip_code      varchar(10),
    roles         varchar(1000),
    creation_date timestamptz default now(),
    fail_attempts int default 0,
    locked_until  bigint
);

create table if not exists password_recovery
(
    id             bigint DEFAULT nextval('password_recovery_seq') PRIMARY KEY,
    user_name      varchar(255),
    recovery_token varchar(6),
    expiry_date    bigint
);

create table if not exists jwt_metadata
(
    id text     not null PRIMARY KEY,
    user_name   varchar(255),
    expiry_date bigint,
    revoked     boolean default false
);

-- create or replace view valid_recovery_token as
--     select * from password_recovery where expiry_date <= now();

delete from user_account where id < 0;

insert into user_account (id, user_name, password, roles) values (-1, 'admin@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'ADMIN');
insert into user_account (id, user_name, password, roles) values (-2, 'admin2@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'ADMIN,USER');
insert into user_account (id, user_name, password, roles) values (-3, 'user@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'USER');
insert into user_account (id, user_name, password, roles, locked_until) values (-4, 'locked@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'USER', 7258118399000);
insert into user_account (id, user_name, password, roles, locked_until) values (-5, 'locked2@example.com', '3ed25143e5d856a2e113f3e53f80e1e09927c66c8b9e28908d55f29d59729aa1', 'USER', 7258118399000);
