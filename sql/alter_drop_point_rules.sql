DELIMITER //

CREATE PROCEDURE DropPointRules()
BEGIN
    -- Drop FK if exists
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

    -- Drop table point_rules if exists
    DROP TABLE IF EXISTS point_rules;
END //

DELIMITER ;

CALL DropPointRules();
DROP PROCEDURE DropPointRules;
