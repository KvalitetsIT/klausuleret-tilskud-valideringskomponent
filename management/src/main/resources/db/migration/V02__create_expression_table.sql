CREATE TABLE expression (
    id INT PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    CHECK (type IN ('condition_expression', 'binary_expression', 'parenthesized_expression')),
    FOREIGN KEY (id) REFERENCES clause(id) ON DELETE CASCADE
);