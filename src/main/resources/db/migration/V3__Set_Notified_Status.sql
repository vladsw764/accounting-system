UPDATE debts
SET is_notified = false
WHERE is_notified IS NULL;
