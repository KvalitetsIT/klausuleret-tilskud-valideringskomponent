CREATE TABLE error_code (
    error_code INT PRIMARY KEY DEFAULT (NEXT VALUE FOR error_code_seq),
    clause_id INT NOT NULL
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Error codes from 10800 to 10999 (max 199 rows)';
