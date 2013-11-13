# Associate folders with user
 
# --- !Ups

ALTER TABLE folders ADD COLUMN user_id BIGINT references users(id);

# --- !Downs
 
ALTER TABLE folders DROP COLUMN user_id;
