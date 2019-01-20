# --- !Ups

create table users (
  id        SERIAL not NULL,
  emailAddress      VARCHAR(255),
  password VARCHAR(255),
  PRIMARY KEY (id)
);

# --- !Downs

drop TABLE if EXISTS users;