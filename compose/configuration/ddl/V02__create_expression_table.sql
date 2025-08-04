CREATE TABLE expression (
    id INT PRIMARY KEY AUTO_INCREMENT,
    clause_id INT NOT NULL,
    type VARCHAR(32) NOT NULL,
    CHECK (type IN ('condition_expression', 'binary_expression', 'parenthesized_expression')),
    FOREIGN KEY (clause_id) REFERENCES clause(id) ON DELETE CASCADE
);