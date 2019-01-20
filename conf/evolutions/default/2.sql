# --- !Ups

create table data (
  id                SERIAL not NULL,
  userid            INT,
  information       VARCHAR(255),
  FOREIGN KEY (userid) REFERENCES users(id),
  PRIMARY KEY (id)
);

# --- !Downs

drop TABLE if EXISTS data;