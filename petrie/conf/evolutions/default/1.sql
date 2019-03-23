# --- !Ups

CREATE TABLE request_history(
  "id" SERIAL,
  url varchar(255),
  depth BIGINT,
  "date" timestamp
);

# --- !Downs

DROP TABLE request_history;