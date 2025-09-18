CREATE TABLE error_code (
    error_code INT PRIMARY KEY,
    clause_name VARCHAR(255) UNIQUE NOT NULL
) AUTO_INCREMENT = 10800
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Error codes from 10800 to 10999 (max 200 rows)';

DELIMITER //
CREATE TRIGGER error_code_check
BEFORE INSERT ON error_code
FOR EACH ROW
BEGIN
    IF NEW.error_code < 10800 OR NEW.error_code > 10999 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error code must be between 10800 and 10999';
    END IF;
END;
//
DELIMITER ;
