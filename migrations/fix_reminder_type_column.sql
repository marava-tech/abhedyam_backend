-- Fix reminder type column size to accommodate longer enum values like SYNC_CALL_LOG
ALTER TABLE reminders MODIFY COLUMN type VARCHAR(50) NOT NULL;
ALTER TABLE reminders MODIFY COLUMN channel VARCHAR(50) NOT NULL;
ALTER TABLE reminders MODIFY COLUMN status VARCHAR(50) NOT NULL;

