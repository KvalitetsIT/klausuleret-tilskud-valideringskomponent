CREATE TABLE binary_expression (
    expression_id INT PRIMARY KEY,
    left_id INT NOT NULL UNIQUE,
    operator VARCHAR(255) NOT NULL,
    right_id INT NOT NULL UNIQUE,
    FOREIGN KEY (expression_id) REFERENCES expression(id) ON DELETE CASCADE,
    FOREIGN KEY (left_id) REFERENCES expression(id),
    FOREIGN KEY (right_id) REFERENCES expression(id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin
  COMMENT='Specialized expression combining two expressions with an operator (AND/OR/etc.)';