CREATE TABLE condition_value (
    id INT AUTO_INCREMENT PRIMARY KEY,
    condition_id INT NOT NULL,
    value VARCHAR(255) NOT NULL,
    FOREIGN KEY (condition_id) REFERENCES `condition_expression`(expression_id) ON DELETE CASCADE
);
