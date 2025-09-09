CREATE TABLE expression (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('CONDITION', 'BINARY') NOT NULL
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Contains all expressions, which can be condition or binary';