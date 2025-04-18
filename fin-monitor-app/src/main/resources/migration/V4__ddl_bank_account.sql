SET search_path TO fin_app;

-- Таблица пользователей
ALTER TABLE bank_accounts ADD COLUMN account_name VARCHAR(100);