DELIMITER //

CREATE PROCEDURE AlterPointTransactions()
BEGIN
    -- Rename collection_id to collection_request_id if it exists
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'point_transactions' 
        AND column_name = 'collection_id' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions RENAME COLUMN collection_id TO collection_request_id;
    END IF;

    -- Add report_id if not exists
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'point_transactions' 
        AND column_name = 'report_id' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions ADD COLUMN report_id INT NULL;
    END IF;

    -- Add collection_request_id if not exists (in case it wasn't renamed or created)
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'point_transactions' 
        AND column_name = 'collection_request_id' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions ADD COLUMN collection_request_id INT NULL;
    END IF;

    -- Add FK report_id
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_point_transactions_report' 
        AND table_name = 'point_transactions' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions 
        ADD CONSTRAINT fk_point_transactions_report FOREIGN KEY (report_id) REFERENCES waste_reports(id);
    END IF;

    -- Add FK collection_request_id
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_point_transactions_collection_request' 
        AND table_name = 'point_transactions' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions 
        ADD CONSTRAINT fk_point_transactions_collection_request FOREIGN KEY (collection_request_id) REFERENCES collection_requests(id);
    END IF;

    -- Drop FK rule if exists
    IF EXISTS (
        SELECT 1 
        FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_point_transactions_rule' 
        AND table_name = 'point_transactions' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions DROP FOREIGN KEY fk_point_transactions_rule;
    END IF;

    -- Drop column rule_id if exists
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'point_transactions' 
        AND column_name = 'rule_id' 
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE point_transactions DROP COLUMN rule_id;
    END IF;

END //

DELIMITER ;

CALL AlterPointTransactions();
DROP PROCEDURE AlterPointTransactions;
