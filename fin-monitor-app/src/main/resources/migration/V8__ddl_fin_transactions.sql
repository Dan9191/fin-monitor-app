SET search_path TO fin_app;

-- Таблица операций
ALTER TABLE fin_transactions ADD COLUMN withdrawal_account VARCHAR(100);