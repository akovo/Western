create table users (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(60) NOT NULL
);