package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ExpressionRepositoryImpl implements ExpressionRepository {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionRepositoryImpl.class);
    private final NamedParameterJdbcTemplate template;
    private final DataClassRowMapper<ExpressionEntity.Persisted.ExistingDrugMedicationCondition> existingDrugMedicationRowMapper;

    public ExpressionRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        existingDrugMedicationRowMapper = new DataClassRowMapper<>(ExpressionEntity.Persisted.ExistingDrugMedicationCondition.class);
    }

    @Override
    public List<ExpressionEntity.Persisted> readAll() throws ServiceException {
        return List.of();
    }

    @Override
    public Optional<ExpressionEntity.Persisted> read(Long id) throws ServiceException {
        try {
            String sql = "SELECT type FROM expression WHERE id = :id";
            ExpressionType type = template.queryForObject(sql, Map.of("id", id), ExpressionType.class);

            assert type != null;

            return switch (type) {
                case STRING_CONDITION -> readStringCondition(id);
                case NUMBER_CONDITION -> readNumberCondition(id);
                case BINARY -> readBinary(id);
                case EXISTING_DRUG_MEDICATION -> readExistingDrugMedicationCondition(id);
            };
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ExpressionEntity.Persisted create(ExpressionEntity.NotPersisted expression) throws ServiceException {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(
                "INSERT INTO expression (type) VALUES (:type)",
                new MapSqlParameterSource().addValue("type", expression.type().toString()),
                keyHolder,
                new String[]{"id"}
        );

        long expressionId = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new ServiceException("Failed to get expression primary key"))
                .longValue();

        return switch (expression) {
            case ExpressionEntity.NotPersisted.StringConditionEntity e -> insertStringCondition(expressionId, e);
            case ExpressionEntity.NotPersisted.NumberCondition e -> insertNumberCondition(expressionId, e);
            case ExpressionEntity.NotPersisted.BinaryExpression e -> insertBinary(expressionId, e);
            case ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition e ->
                    insertExistingDrugMedication(expressionId, e);
        };
    }

    private ExpressionEntity.Persisted.StringCondition insertStringCondition(Long expressionId, ExpressionEntity.NotPersisted.StringConditionEntity condition) {
        template.update(
                "INSERT INTO string_condition_expression(expression_id, field, value) VALUES (:expression_id, :field, :value)",
                Map.of(
                        "expression_id", expressionId,
                        "field", condition.field().name(),
                        "value", condition.value()
                ));
        return new ExpressionEntity.Persisted.StringCondition(expressionId, condition.field(), condition.value());
    }

    private ExpressionEntity.Persisted.NumberCondition insertNumberCondition(Long expressionId, ExpressionEntity.NotPersisted.NumberCondition condition) {
        template.update(
                "INSERT INTO number_condition_expression(expression_id, field, operator, value) VALUES (:expression_id, :field, :operator, :value)",
                Map.of(
                        "expression_id", expressionId,
                        "field", condition.field().name(),
                        "operator", condition.operator().getValue(),
                        "value", condition.value()
                ));

        return new ExpressionEntity.Persisted.NumberCondition(expressionId, condition.field(), condition.operator(), condition.value());
    }

    private ExpressionEntity.Persisted.ExistingDrugMedicationCondition insertExistingDrugMedication(Long expressionId, ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition existingDrugMedication) {
        template.update(
                "INSERT INTO existing_drug_medication_condition_expression(expression_id, atc_code, form_code, route_of_administration_code) VALUES (:expression_id, :atc_code, :form_code, :route_of_administration_code)",
                Map.of(
                        "expression_id", expressionId,
                        "atc_code", existingDrugMedication.atcCode(),
                        "form_code", existingDrugMedication.formCode(),
                        "route_of_administration_code", existingDrugMedication.routeOfAdministrationCode()
                ));
        return new ExpressionEntity.Persisted.ExistingDrugMedicationCondition(expressionId, existingDrugMedication.atcCode(), existingDrugMedication.formCode(), existingDrugMedication.routeOfAdministrationCode());
    }

    private ExpressionEntity.Persisted.BinaryExpression insertBinary(Long expressionId, ExpressionEntity.NotPersisted.BinaryExpression binary) {
        ExpressionEntity.Persisted left = create(binary.left());
        ExpressionEntity.Persisted right = create(binary.right());

        template.update(
                "INSERT INTO binary_expression(expression_id, left_id, operator, right_id) VALUES (:expression_id, :left_id, :operator, :right_id)",
                Map.of(
                        "expression_id", expressionId,
                        "left_id", left.id(),
                        "operator", binary.operator().toString(),
                        "right_id", right.id()
                )
        );
        return new ExpressionEntity.Persisted.BinaryExpression(expressionId, left, binary.operator(), right);
    }


    private Optional<ExpressionEntity.Persisted> readStringCondition(long id) {
        try {
            ExpressionEntity.Persisted.StringCondition result = template.queryForObject(
                    "SELECT field, value FROM string_condition_expression WHERE expression_id = :id",
                    Map.of("id", id),
                    (rs, rowNum) -> new ExpressionEntity.Persisted.StringCondition(
                            id,
                            Field.valueOf(rs.getString("field")),
                            rs.getString("value"))
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Failed to read expression {}", id, e);
            return Optional.empty();
        }
    }

    private Optional<ExpressionEntity.Persisted> readNumberCondition(long id) {
        try {
            var result = template.queryForObject(
                    "SELECT field, operator, value FROM number_condition_expression WHERE expression_id = :id",
                    Map.of("id", id),
                    (rs, rowNum) -> new ExpressionEntity.Persisted.NumberCondition(
                            id,
                            Field.valueOf(rs.getString("field")),
                            Operator.fromValue(rs.getString("operator")),
                            rs.getInt("value")));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Failed to read expression {}", id, e);
            return Optional.empty();
        }
    }

    private Optional<ExpressionEntity.Persisted> readExistingDrugMedicationCondition(long id) {
        try {
            var result = template.queryForObject(
                    "SELECT expression_id as id, atc_code, form_code, route_of_administration_code FROM existing_drug_medication_condition_expression WHERE expression_id = :id",
                    Map.of("id", id),
                    existingDrugMedicationRowMapper

            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Failed to read expression {}", id, e);
            return Optional.empty();
        }
    }

    private Optional<ExpressionEntity.Persisted> readBinary(Long id) {
        try {
            var result = template.queryForObject(
                    "SELECT left_id, operator, right_id FROM binary_expression WHERE expression_id = :id",
                    Map.of("id", id),
                    (rs, rowNum) -> new ExpressionEntity.Persisted.BinaryExpression(
                            id,
                            read(rs.getLong("left_id")).orElseThrow(),
                            BinaryOperator.valueOf(rs.getString("operator")),
                            read(rs.getLong("right_id")).orElseThrow()
                    )
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Failed to read expression {}", id, e);
            return Optional.empty();
        }
    }


}
