IF COL_LENGTH('point_transactions', 'collection_id') IS NOT NULL
    AND COL_LENGTH('point_transactions', 'collection_request_id') IS NULL
BEGIN
    EXEC sp_rename 'point_transactions.collection_id', 'collection_request_id', 'COLUMN';
END
GO

IF COL_LENGTH('point_transactions', 'report_id') IS NULL
BEGIN
    ALTER TABLE point_transactions ADD report_id INT NULL;
END
GO

IF COL_LENGTH('point_transactions', 'collection_request_id') IS NULL
BEGIN
    ALTER TABLE point_transactions ADD collection_request_id INT NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'fk_point_transactions_report'
)
BEGIN
    ALTER TABLE point_transactions
        ADD CONSTRAINT fk_point_transactions_report
        FOREIGN KEY (report_id) REFERENCES waste_reports(id);
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'fk_point_transactions_collection_request'
)
BEGIN
    ALTER TABLE point_transactions
        ADD CONSTRAINT fk_point_transactions_collection_request
        FOREIGN KEY (collection_request_id) REFERENCES collection_requests(id);
END
GO

IF EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'fk_point_transactions_rule'
)
BEGIN
    ALTER TABLE point_transactions DROP CONSTRAINT fk_point_transactions_rule;
END
GO

IF COL_LENGTH('point_transactions', 'rule_id') IS NOT NULL
BEGIN
    ALTER TABLE point_transactions DROP COLUMN rule_id;
END
GO
