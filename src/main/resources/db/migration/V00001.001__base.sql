-- noinspection SqlNoDataSourceInspectionForFile
create sequence if not exists passwordrecovery_seq start with 1 increment by 1;

create table if not exists passwordrecovery
(
    id             bigint DEFAULT nextval('passwordrecovery_seq') PRIMARY KEY,
    emailAddress   varchar(320),
    recoveryToken  varchar(6),
    expiryDate     timestamptz
);

create table if not exists jwtmetadata
(
    id            text not null PRIMARY KEY,
    emailAddress  varchar(320),
    expiryDate    timestamptz,
    revoked       boolean default false
);
