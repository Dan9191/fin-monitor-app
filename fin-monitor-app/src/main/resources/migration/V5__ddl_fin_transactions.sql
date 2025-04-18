SET search_path TO fin_app;

-- Таблица пользователей
ALTER TABLE fin_transactions ADD COLUMN create_date TIMESTAMP NOT NULL DEFAULT NOW();