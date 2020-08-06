create table users (
  id varchar(128) PRIMARY KEY,
  username varchar(128) UNIQUE,
  password varchar(128),
  email varchar(128) UNIQUE,
  bio text,
  image varchar(256)
);