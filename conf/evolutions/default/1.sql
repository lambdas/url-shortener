# Create users table
 
# --- !Ups

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    secret varchar(255) NOT NULL,
    token varchar(255) NOT NULL
);
 
# --- !Downs
 
DROP TABLE users;
