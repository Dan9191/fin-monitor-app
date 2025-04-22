SET search_path TO fin_app;

-- Узнаем имя текущего ограничения (если не уверены)
SELECT conname
FROM pg_constraint
WHERE conrelid = 'fin_transactions'::regclass
AND confrelid = 'bank_accounts'::regclass;

-- Удаляем старое ограничение (подставьте правильное имя)
ALTER TABLE fin_transactions
DROP CONSTRAINT fin_transactions_bank_account_id_fkey;

-- Добавляем новое ограничение с CASCADE
ALTER TABLE fin_transactions
    ADD CONSTRAINT fin_transactions_bank_account_id_fkey
        FOREIGN KEY (bank_account_id)
            REFERENCES bank_accounts(id)
            ON DELETE CASCADE;