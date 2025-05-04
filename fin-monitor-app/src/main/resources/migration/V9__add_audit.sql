CREATE TABLE IF NOT EXISTS fin_app.audit_integration_logs
(
    id                   BIGSERIAL PRIMARY KEY,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    service_name         VARCHAR(128)             NOT NULL,                                             -- Название внешнего сервиса
    endpoint             VARCHAR(256)             NOT NULL,                                             -- URL или идентификатор endpoint
    request_method       VARCHAR(16)              NOT NULL,                                             -- HTTP метод (GET, POST и т.д.)
    request_headers      TEXT,                                                                          -- Заголовки запроса
    request_body         TEXT,                                                                          -- Тело запроса (если есть)
    request_parameters   TEXT,                                                                          -- Параметры запроса
    response_status_code INTEGER,                                                                       -- HTTP статус код ответа
    response_headers     TEXT,                                                                          -- Заголовки ответа
    response_body        TEXT,                                                                          -- Тело ответа
    response_time_ms     INTEGER,                                                                       -- Время выполнения запроса в мс
    success              BOOLEAN                  NOT NULL,                                             -- Флаг успешности операции
    error_message        TEXT,                                                                          -- Сообщение об ошибке (если есть)
    correlation_id       VARCHAR(128),                                                                  -- ID для корреляции запросов
    additional_context   JSONB,                                                                         -- Дополнительный контекст
    retry_count          INTEGER                  DEFAULT 0,                                            -- Количество попыток (для retry)
    direction            VARCHAR(16)              NOT NULL CHECK (direction IN ('OUTBOUND', 'INBOUND')) -- Направление вызова
);


CREATE TABLE IF NOT EXISTS fin_app.audit_internal_logs
(
    "id"         SERIAL PRIMARY KEY,
    "table_name" VARCHAR(128) NOT NULL,
    operation    CHAR(6)      NOT NULL,
    record_id    INTEGER,     -- ID изменяемой записи (может быть другим типом в зависимости от ваших ID)
    old_data     JSONB,       -- Данные до изменения (для UPDATE и DELETE)
    new_data     JSONB,       -- Данные после изменения (для INSERT и UPDATE)
    changed_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    changed_by   VARCHAR(128) -- Пользователь, внесший изменения
);


CREATE OR REPLACE FUNCTION fin_app.audit_internal_log_function()
    RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO fin_app.audit_internal_logs ("table_name",
                                                 operation,
                                                 record_id,
                                                 new_data,
                                                 changed_by)
        VALUES (TG_TABLE_NAME,
                TG_OP,
                NEW.id, -- предполагается, что есть поле id
                to_jsonb(NEW),
                current_user);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO fin_app.audit_internal_logs ("table_name",
                                                 operation,
                                                 record_id,
                                                 old_data,
                                                 new_data,
                                                 changed_by)
        VALUES (TG_TABLE_NAME,
                TG_OP,
                NEW.id,
                to_jsonb(OLD),
                to_jsonb(NEW),
                current_user);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO fin_app.audit_internal_logs ("table_name",
                                                 operation,
                                                 record_id,
                                                 old_data,
                                                 changed_by)
        VALUES (TG_TABLE_NAME,
                TG_OP,
                OLD.id,
                to_jsonb(OLD),
                current_user);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER users_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON fin_app.users
    FOR EACH ROW
EXECUTE FUNCTION fin_app.audit_internal_log_function();


CREATE OR REPLACE TRIGGER bank_accounts_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON fin_app.bank_accounts
    FOR EACH ROW
EXECUTE FUNCTION fin_app.audit_internal_log_function();


CREATE OR REPLACE TRIGGER fin_transactions_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON fin_app.fin_transactions
    FOR EACH ROW
EXECUTE FUNCTION fin_app.audit_internal_log_function();

