SET search_path TO fin_app;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
                       id SERIAL PRIMARY KEY,
                       login VARCHAR(50) NOT NULL UNIQUE,
                       "name" VARCHAR(500),
                       email VARCHAR(500),
                       password VARCHAR(100) NOT NULL
);

COMMENT ON TABLE users IS 'Таблица для хранения информации о пользователях системы';
COMMENT ON COLUMN users.id IS 'Идентификатор пользователя';
COMMENT ON COLUMN users.login IS 'Логин для входа в систему (уникальный)';
COMMENT ON COLUMN users."name" IS 'Имя пользователя';
COMMENT ON COLUMN users.email IS 'Почта';
COMMENT ON COLUMN users.password IS 'Хэшированный пароль пользователя';

-- Таблица банковских счетов
CREATE TABLE IF NOT EXISTS bank_accounts (
                               id SERIAL PRIMARY KEY,
                               user_id INTEGER NOT NULL,
                               person_type_id INTEGER NOT NULL,
                               account_number VARCHAR(20) NOT NULL UNIQUE,
                               balance NUMERIC(15,5) NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               FOREIGN KEY (person_type_id) REFERENCES person_type(id)
);

COMMENT ON TABLE bank_accounts IS 'Таблица для хранения информации о банковских счетах';
COMMENT ON COLUMN bank_accounts.id IS 'Идентификатор счета';
COMMENT ON COLUMN bank_accounts.user_id IS 'Ссылка на владельца счета';
COMMENT ON COLUMN bank_accounts.person_type_id IS 'Тип владельца (физ/юр лицо)';
COMMENT ON COLUMN bank_accounts.account_number IS 'Номер банковского счета';
COMMENT ON COLUMN bank_accounts.balance IS 'Текущий баланс счета (точность 5 знаков)';

-- Таблица финансовых транзакций
CREATE TABLE IF NOT EXISTS fin_transactions (
                                  id SERIAL PRIMARY KEY,
                                  operation_status_id INTEGER NOT NULL,
                                  bank_account_id INTEGER NOT NULL,
                                  transaction_type_id INTEGER NOT NULL,
                                  commentary VARCHAR(500),
                                  sum NUMERIC(15,5) NOT NULL,
                                  sender_bank VARCHAR(100),
                                  recipient_bank VARCHAR(100),
                                  recipient_tin VARCHAR(20),
                                  recipient_bank_account VARCHAR(20),
                                  category_id INTEGER,
                                  recipient_telephone_number VARCHAR(20),
                                  FOREIGN KEY (operation_status_id) REFERENCES operation_status(id),
                                  FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id),
                                  FOREIGN KEY (transaction_type_id) REFERENCES transaction_type(id),
                                  FOREIGN KEY (category_id) REFERENCES category(id)
);

COMMENT ON TABLE fin_transactions IS 'Таблица для хранения информации о финансовых транзакциях';
COMMENT ON COLUMN fin_transactions.id IS 'Идентификатор транзакции';
COMMENT ON COLUMN fin_transactions.operation_status_id IS 'Статус операции (например, выполнена, в обработке)';
COMMENT ON COLUMN fin_transactions.bank_account_id IS 'Ссылка на банковский счет';
COMMENT ON COLUMN fin_transactions.transaction_type_id IS 'Тип транзакции';
COMMENT ON COLUMN fin_transactions.commentary IS 'Комментарий к транзакции';
COMMENT ON COLUMN fin_transactions.sum IS 'Сумма транзакции (точность 5 знаков)';
COMMENT ON COLUMN fin_transactions.sender_bank IS 'Банк отправителя';
COMMENT ON COLUMN fin_transactions.recipient_bank IS 'Банк получателя';
COMMENT ON COLUMN fin_transactions.recipient_tin IS 'ИНН получателя';
COMMENT ON COLUMN fin_transactions.recipient_bank_account IS 'Номер счета получателя';
COMMENT ON COLUMN fin_transactions.category_id IS 'Категория транзакции';
COMMENT ON COLUMN fin_transactions.recipient_telephone_number IS 'Телефонный номер получателя';