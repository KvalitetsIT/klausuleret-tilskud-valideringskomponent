CREATE TABLE skipped_validation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    clause_id INT NOT NULL,
    actor_id VARCHAR(255) NOT NULL,
    person_id VARCHAR(255) NOT NULL,
    replaces INT, -- If the skipped validation is replaced. This id points to previous row
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (clause_id) REFERENCES clause(id),
    FOREIGN KEY (replaces) REFERENCES skipped_validation(id) ON DELETE CASCADE,

    CONSTRAINT unique_skipped_validation UNIQUE (clause_id, actor_id, person_id)
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Validations to be skipped';