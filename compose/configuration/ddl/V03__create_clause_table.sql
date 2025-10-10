CREATE TABLE clause (
    id INT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    expression_id INT NOT NULL UNIQUE,
    error_message VARCHAR(255) NOT NULL,
    FOREIGN KEY (expression_id) REFERENCES expression(id) ON DELETE CASCADE
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Represents a clause linked to an expression';