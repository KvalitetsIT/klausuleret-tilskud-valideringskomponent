CREATE TABLE clause (
    id INT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(255),
    expression_id INT UNIQUE,
    FOREIGN KEY (expression_id) REFERENCES expression(id) ON DELETE CASCADE
);