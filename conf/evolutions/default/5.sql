# Create links table
 
# --- !Ups

CREATE TABLE links (
    id SERIAL PRIMARY KEY,
    url varchar(255) NOT NULL,
    code varchar(255) NOT NULL UNIQUE,
    user_id BIGINT references users(id) NOT NULL,
    folder_id BIGINT references folders(id)
);
 
# --- !Downs
 
DROP TABLE links;
