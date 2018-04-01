--liquibase formatted sql

--changeset kilel:1
create table SN_USERS (
  id   bigint       not null primary key auto_increment,
  code longvarchar not null unique,
  dscr longvarchar
);

--changeset kilel:2
create index I_SN_USERS_ID
  ON SN_USERS (id);
create index I_SN_USERS_CODE
  ON SN_USERS (code);