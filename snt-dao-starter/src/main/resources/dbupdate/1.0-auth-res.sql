--liquibase formatted sql

--changeset kilel:1
create table SN_AUTH_RES (
  id      bigint      not null  primary key auto_increment,
  user_id bigint      not null,
  code    longvarchar not null,
  dscr    longvarchar,
  type_id bigint,
  data    longvarbinary,
  data_ck longvarbinary,

  constraint UK_SN_AUTH_RES unique (user_id, code),

  constraint FK_SN_AUTH_RES_USER_ID
  foreign key (user_id) REFERENCES SN_USERS (id) on delete cascade
);

--changeset kilel:2
create index I_SN_AUTH_RES_ID
  ON SN_AUTH_RES (id);
create index I_SN_AUTH_RES_CODE
  ON SN_AUTH_RES (user_id, code);