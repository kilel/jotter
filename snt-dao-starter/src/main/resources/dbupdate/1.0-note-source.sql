--liquibase formatted sql

--changeset kilel:1
create table SN_NOTE_SRC (
  id      bigint not null  primary key auto_increment,
  user_id bigint not null,
  note_id bigint not null,
  dscr    longvarchar,

  constraint UK_SN_NOTE_SRC unique (user_id, note_id),

  constraint FK_SN_NOTE_SRC_USER_ID
  foreign key (user_id) REFERENCES SN_USERS (id) on delete cascade,

  constraint FK_SN_NOTE_SRC_NOTE_ID
  foreign key (note_id) REFERENCES SN_NOTES (id) on delete cascade
);

--changeset kilel:2
create index I_SN_NOTE_SRC_ID
  ON SN_NOTE_SRC (id);
create index I_SN_NOTE_SRC_USER
  ON SN_NOTE_SRC (user_id, note_id);