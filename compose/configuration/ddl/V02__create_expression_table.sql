CREATE TABLE expression (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('STRING_CONDITION', 'NUMBER_CONDITION', 'BINARY', 'PREVIOUS_ORDINATION') NOT NULL
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Contains all expressions, which can be condition or binary';