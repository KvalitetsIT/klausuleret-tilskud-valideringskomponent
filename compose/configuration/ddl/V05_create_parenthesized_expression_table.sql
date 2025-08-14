CREATE TABLE parenthesized_expression (
    expression_id INT PRIMARY KEY,
    inner_id INT NOT NULL UNIQUE,
    FOREIGN KEY (expression_id) REFERENCES expression(id) ON DELETE CASCADE,
    FOREIGN KEY (inner_id) REFERENCES expression(id)
);