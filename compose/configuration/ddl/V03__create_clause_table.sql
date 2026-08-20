CREATE TABLE clause (
    id INT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    expression_id INT NOT NULL UNIQUE,
    error_message VARCHAR(255) NOT NULL,
    status ENUM('DRAFT','ACTIVE', 'INACTIVE') NOT NULL,
    created_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    created_by VARCHAR(255) NOT NULL,
    primary_parent_id INT NULL UNIQUE COMMENT 'Parent clause from the same state', -- For a draft, it is the previous version of the draft. For an active or inactive clause, it is the previous active or inactive version of the clause.
    secondary_parent_id INT NULL UNIQUE COMMENT 'Parent clause from a different state',
    FOREIGN KEY (expression_id) REFERENCES expression(id),
    FOREIGN KEY (primary_parent_id) REFERENCES clause(id),
    FOREIGN KEY (secondary_parent_id) REFERENCES clause(id)
)
DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT='Represents a clause linked to an expression';