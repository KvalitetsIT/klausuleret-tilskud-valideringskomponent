CREATE TABLE error_code (
    error_code INT PRIMARY KEY,
    clause_name VARCHAR(255) UNIQUE NOT NULL
) AUTO_INCREMENT = 10800
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Error codes from 10800 to 10999 (max 200 rows)';
