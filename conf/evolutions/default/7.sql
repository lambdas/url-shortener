# Add click count to links
 
# --- !Ups

ALTER TABLE links ADD COLUMN click_count BIGINT NOT NULL DEFAULT 0;

# --- !Downs
 
ALTER TABLE links DROP COLUMN click_count;
