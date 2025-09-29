CREATE TABLE skipped_validation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    clause_id INT NOT NULL,
    actor_id VARCHAR(255) NOT NULL,
    person_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (clause_id) REFERENCES clause(id)
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Validations to be skipped';