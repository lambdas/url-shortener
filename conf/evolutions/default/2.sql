# Create folders table
 
# --- !Ups

CREATE TABLE folders (
    id SERIAL PRIMARY KEY,
    title varchar(255) UNIQUE NOT NULL
);
 
# --- !Downs
 
DROP TABLE folders;
