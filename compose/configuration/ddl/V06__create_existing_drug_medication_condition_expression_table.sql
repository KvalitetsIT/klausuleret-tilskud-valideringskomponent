CREATE TABLE existing_drug_medication_condition_expression (
    expression_id INT PRIMARY KEY,
    atc_code VARCHAR(255) NOT NULL,
    form_code VARCHAR(255) NOT NULL,
    route_of_administration_code VARCHAR(255) NOT NULL,
    FOREIGN KEY (expression_id) REFERENCES expression(id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin
  COMMENT='Specialized expression representing a condition for existing drug medication';
;