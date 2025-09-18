CREATE TABLE `number_condition_expression` (
    expression_id INT PRIMARY KEY,
    field VARCHAR(255) NOT NULL,
    operator ENUM('=','>','<','>=','<=') NOT NULL,
    value INT NOT NULL,
    FOREIGN KEY (expression_id) REFERENCES expression(id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin
  COMMENT='Specialized expression representing a condition with a number value (field + operator + value)';