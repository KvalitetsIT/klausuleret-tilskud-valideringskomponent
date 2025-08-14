CREATE TABLE expression (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(32) NOT NULL,
    CHECK (type IN ('condition_expression', 'binary_expression', 'parenthesized_expression'))
);