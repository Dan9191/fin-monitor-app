SET search_path TO fin_app;

-- Таблица пользователей
ALTER TABLE users ADD COLUMN phone VARCHAR(100);