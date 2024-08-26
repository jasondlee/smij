-- noinspection SqlNoDataSourceInspectionForFile
create sequence if not exists password_recovery_seq start with 1 increment by 1;

create table if not exists password_recovery
(
    id             bigint DEFAULT nextval('password_recovery_seq') PRIMARY KEY,
    user_name      varchar(320),
    recovery_token varchar(6),
    expiry_date    timestamptz
);

create table if not exists jwt_metadata
(
    id          text not null PRIMARY KEY,
    user_name   varchar(320),
    expiry_date timestamptz,
    revoked     boolean default false
);
