--liquibase formatted sql

--changeset kilel:1
create table SN_NOTES (
  id        bigint      not null primary key auto_increment,
  id_hi     bigint,
  code      longvarchar not null,
  dscr      longvarchar,
  data      longvarbinary,
  schema_id bigint,
  update_dt timestamp default CURRENT_TIMESTAMP,

  constraint UK_SN_NOTES unique (id, id_hi),

  constraint FK_SN_NOTES_ID_HI
  foreign key (id_hi) REFERENCES SN_NOTES (id) on delete set null
);

--changeset kilel:2
create index I_SN_NOTES_ID
  ON SN_NOTES (id);