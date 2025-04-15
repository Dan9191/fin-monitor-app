SET search_path TO fin_app;

-- Создание таблицы "Тип лица"
CREATE TABLE IF NOT EXISTS person_type (id SERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE);

INSERT INTO person_type (id, name) SELECT 1, 'Физическое лицо'
    WHERE NOT EXISTS (SELECT 1 FROM person_type WHERE id = 1);
INSERT INTO person_type (id, name) SELECT 2, 'Юридическое лицо'
    WHERE NOT EXISTS (SELECT 1 FROM person_type WHERE id = 2);

-- Создание таблицы "Тип транзакции"
CREATE TABLE IF NOT EXISTS transaction_type (id SERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE);

INSERT INTO transaction_type (id, name)SELECT 1, 'Поступление'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_type WHERE id = 1);
INSERT INTO transaction_type (id, name)SELECT 2, 'Списание'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_type WHERE id = 2);


-- Создание таблицы "Статус операции"
CREATE TABLE IF NOT EXISTS operation_status (id SERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE);

-- Добавление записей в "Статус операции" с проверкой
INSERT INTO operation_status (id, name) SELECT 1, 'Новая'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 1);
INSERT INTO operation_status (id, name) SELECT 2, 'Подтвержденная'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 2);
INSERT INTO operation_status (id, name) SELECT 3, 'В обработке'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 3);
INSERT INTO operation_status (id, name) SELECT 4, 'Отменена'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 4);
INSERT INTO operation_status (id, name) SELECT 5, 'Платеж выполнен'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 5);
INSERT INTO operation_status (id, name) SELECT 6, 'Платеж удален'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 6);
INSERT INTO operation_status (id, name) SELECT 7, 'Возврат'
    WHERE NOT EXISTS (SELECT 1 FROM operation_status WHERE id = 7);

-- Создание таблицы "Категория"
CREATE TABLE IF NOT EXISTS category (id SERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE );

-- Добавление записей в "Категория" с проверкой
INSERT INTO category (id, name) SELECT 1, 'Продукты питания'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 1);
INSERT INTO category (id, name) SELECT 2, 'Транспорт'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 2);
INSERT INTO category (id, name) SELECT 3, 'Жилье (аренда/ипотека)'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 3);
INSERT INTO category (id, name) SELECT 4, 'Коммунальные услуги'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 4);
INSERT INTO category (id, name) SELECT 5, 'Здоровье и медицина'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 5);
INSERT INTO category (id, name) SELECT 6, 'Образование'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 6);
INSERT INTO category (id, name) SELECT 7, 'Развлечения'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 7);
INSERT INTO category (id, name) SELECT 8, 'Одежда и обувь'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 8);
INSERT INTO category (id, name) SELECT 9, 'Техника и электроника'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 9);
INSERT INTO category (id, name) SELECT 10, 'Подарки'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 10);
INSERT INTO category (id, name) SELECT 11, 'Рестораны и кафе'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 11);
INSERT INTO category (id, name) SELECT 12, 'Путешествия'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 12);
INSERT INTO category (id, name) SELECT 13, 'Спорт'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 13);
INSERT INTO category (id, name) SELECT 14, 'Красота и уход'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 14);
INSERT INTO category (id, name) SELECT 15, 'Домашние животные'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 15);
INSERT INTO category (id, name) SELECT 16, 'Страховки'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 16);
INSERT INTO category (id, name) SELECT 17, 'Налоги'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 17);
INSERT INTO category (id, name) SELECT 18, 'Инвестиции'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 18);
INSERT INTO category (id, name) SELECT 19, 'Зарплата'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 19);
INSERT INTO category (id, name) SELECT 20, 'Фриланс'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 20);
INSERT INTO category (id, name) SELECT 21, 'Дивиденды'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 21);
INSERT INTO category (id, name) SELECT 22, 'Пенсия'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 22);
INSERT INTO category (id, name) SELECT 23, 'Социальные выплаты'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 23);
INSERT INTO category (id, name) SELECT 24, 'Возврат долга'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 24);
INSERT INTO category (id, name) SELECT 25, 'Прочие доходы'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 25);
INSERT INTO category (id, name) SELECT 26, 'Прочие расходы'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 26);
