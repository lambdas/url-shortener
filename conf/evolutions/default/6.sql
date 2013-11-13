# Create clicks table
 
# --- !Ups

CREATE TABLE clicks (
    id SERIAL PRIMARY KEY,
    link_id BIGINT references links(id) NOT NULL,
    refferer varchar(255) NOT NULL,
    ip varchar(255) NOT NULL,
    created timestamp NOT NULL
);
 
# --- !Downs
 
DROP TABLE clicks;
