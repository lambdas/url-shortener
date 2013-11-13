# Make user tokens unique
 
# --- !Ups

ALTER TABLE users ADD CONSTRAINT token_unique UNIQUE (token);

# --- !Downs
 
ALTER TABLE users DROP CONSTRAINT token_unique;
