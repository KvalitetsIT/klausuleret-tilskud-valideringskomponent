package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
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
import java.util.*;

public class ClauseRepositoryImpl implements ClauseRepository<ClauseEntity> {

    private static final Logger logger = LoggerFactory.getLogger(ClauseRepositoryImpl.class);
    private final NamedParameterJdbcTemplate template;
    private final DataClassRowMapper<ExpressionEntity.PreviousOrdinationEntity> previousOrdinationRowMapper;

    public ClauseRepositoryImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
        previousOrdinationRowMapper = new DataClassRowMapper<>(ExpressionEntity.PreviousOrdinationEntity.class);
    }

    @Override
    public ClauseEntity create(ClauseEntity clause) throws ServiceException {
        try {
            UUID uuid = UUID.randomUUID();
            KeyHolder keyHolder = new GeneratedKeyHolder();

            ExpressionEntity expression = create(clause.expression());

            template.update(
                    "INSERT INTO clause (uuid, name, expression_id) VALUES (:uuid, :name, :expression_id)",
                    new MapSqlParameterSource()
                            .addValue("uuid", uuid.toString())
                            .addValue("name", clause.name())
                            .addValue("expression_id", expression.id()),
                    keyHolder,
                    new String[]{"id"}
            );

            long clauseId = Optional.ofNullable(keyHolder.getKey())
                    .orElseThrow(() -> new ServiceException("Failed to generate clause primary key"))
                    .longValue();

            createErrorCode(clause.name());

            return new ClauseEntity(clauseId, uuid, clause.name(), expression);

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
        }
    }


    public synchronized void createErrorCode(String clauseName) {
        Long max = template.getJdbcTemplate().queryForObject(
                "SELECT COALESCE(MAX(error_code), 10799) FROM error_code",
                Long.class
        );

        long next = max + 1;

        if (next > 10999) {
            throw new IllegalStateException("Exceeded the maximum number of allocated error codes (10800–10999 exhausted)");
        }

        template.update(
                "INSERT INTO error_code (error_code, clause_name) VALUES (:error_code, :clause_name)",
                new MapSqlParameterSource()
                        .addValue("error_code", next)
                        .addValue("clause_name", clauseName)
        );
    }

    private ExpressionEntity create(ExpressionEntity expression) {

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

        return switch (expression.withId(expressionId)) {
            case ExpressionEntity.StringConditionEntity e -> insertStringCondition(e);
            case ExpressionEntity.NumberConditionEntity e -> insertNumberCondition(e);
            case ExpressionEntity.BinaryExpressionEntity e -> insertBinary(e);
            case ExpressionEntity.PreviousOrdinationEntity e -> insertPreviousOrdination(e);
        };
    }

    @Override
    public Optional<ClauseEntity> read(UUID uuid) throws ServiceException {
        try {
            String sql = """
                        SELECT c.id, c.name, c.expression_id, e.type
                        FROM clause c
                        JOIN expression e ON c.expression_id = e.id
                        WHERE c.uuid = :uuid
                    """;

            Map<String, Object> row = template.queryForMap(sql, Map.of("uuid", uuid.toString()));

            Long id = ((Number) row.get("id")).longValue();
            String name = (String) row.get("name");
            ExpressionType type = ExpressionType.valueOf((String) row.get("type"));
            Long expressionId = ((Number) row.get("expression_id")).longValue();

            ExpressionEntity expression = readExpression(type, expressionId);

            return Optional.of(new ClauseEntity(id, uuid, name, expression));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to read expression {}", uuid, e);
            throw new ServiceException("Failed to read expression", e);
        }
    }

    private ExpressionEntity readExpression(ExpressionType type, Long expressionId) {
        return switch (type) {
            case ExpressionType.STRING_CONDITION -> readStringCondition(expressionId);
            case ExpressionType.NUMBER_CONDITION -> readNumberCondition(expressionId);
            case ExpressionType.BINARY -> readBinary(expressionId);
            case PREVIOUS_ORDINATION -> readPreviousOrdination(expressionId);
        };
    }


    @Override
    public List<ClauseEntity> readAll() throws ServiceException {
        try {
            String sql = """
                        SELECT c.uuid
                        FROM clause c
                        JOIN expression e ON c.expression_id = e.id
                        ORDER BY c.id
                    """;

            List<UUID> uuids = template.query(sql, Collections.emptyMap(), (rs, rowNum) ->
                    UUID.fromString(rs.getString("uuid"))
            );

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            logger.error("Failed to read all expressions", e);
            throw new ServiceException("Failed to read expressions", e);
        }
    }

    private ExpressionEntity.StringConditionEntity insertStringCondition(ExpressionEntity.StringConditionEntity condition) {

        template.update(
                "INSERT INTO string_condition_expression(expression_id, field, value) VALUES (:expression_id, :field, :value)",
                Map.of(
                        "expression_id", condition.id(),
                        "field", condition.field().name(),
                        "value", condition.value()
                ));
        return condition;
    }

    private ExpressionEntity.NumberConditionEntity insertNumberCondition(ExpressionEntity.NumberConditionEntity condition) {

        template.update(
                "INSERT INTO number_condition_expression(expression_id, field, operator, value) VALUES (:expression_id, :field, :operator, :value)",
                Map.of(
                        "expression_id", condition.id(),
                        "field", condition.field().name(),
                        "operator", condition.operator().getValue(),
                        "value", condition.value()
                ));
        return condition;
    }

    private ExpressionEntity.PreviousOrdinationEntity insertPreviousOrdination(ExpressionEntity.PreviousOrdinationEntity previousOrdination) {

        template.update(
                "INSERT INTO previous_ordination_expression(expression_id, atc_code, form_code, route_of_administration_code) VALUES (:expression_id, :atc_code, :form_code, :route_of_administration_code)",
                Map.of(
                        "expression_id", previousOrdination.id(),
                        "atc_code", previousOrdination.atcCode(),
                        "form_code", previousOrdination.formCode(),
                        "route_of_administration_code", previousOrdination.routeOfAdministrationCode()
                ));
        return previousOrdination;
    }

    private ExpressionEntity.BinaryExpressionEntity insertBinary(ExpressionEntity.BinaryExpressionEntity binary) {
        ExpressionEntity left = create(binary.left());
        ExpressionEntity right = create(binary.right());

        template.update(
                "INSERT INTO binary_expression(expression_id, left_id, operator, right_id) VALUES (:expression_id, :left_id, :operator, :right_id)",
                Map.of(
                        "expression_id", binary.id(),
                        "left_id", left.id(),
                        "operator", binary.operator().toString(),
                        "right_id", right.id()
                )
        );

        return new ExpressionEntity.BinaryExpressionEntity(binary.id(), left, binary.operator(), right);
    }

    private ExpressionEntity.StringConditionEntity readStringCondition(long expressionId) {
        return template.queryForObject(
                "SELECT field, value FROM string_condition_expression WHERE expression_id = :id",
                Map.of("id", expressionId),
                (rs, rowNum) -> new ExpressionEntity.StringConditionEntity(
                        expressionId,
                        Expression.Condition.Field.valueOf(rs.getString("field")),
                        rs.getString("value"))
        );
    }

    private ExpressionEntity.NumberConditionEntity readNumberCondition(long expressionId) {
        return template.queryForObject(
                "SELECT field, operator, value FROM number_condition_expression WHERE expression_id = :id",
                Map.of("id", expressionId),
                (rs, rowNum) -> new ExpressionEntity.NumberConditionEntity(
                        expressionId,
                        Expression.Condition.Field.valueOf(rs.getString("field")),
                        Operator.fromValue(rs.getString("operator")),
                        rs.getInt("value")));
    }

    private ExpressionEntity.PreviousOrdinationEntity readPreviousOrdination(long expressionId) {
        return template.queryForObject(
                "SELECT expression_id as id, atc_code, form_code, route_of_administration_code FROM previous_ordination_expression WHERE expression_id = :id",
                Map.of("id", expressionId),
                previousOrdinationRowMapper
        );
    }

    private ExpressionEntity readBinary(Long id) {
        return template.queryForObject(
                "SELECT left_id, operator, right_id FROM binary_expression WHERE expression_id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new ExpressionEntity.BinaryExpressionEntity(
                        id,
                        readById(rs.getLong("left_id")).orElseThrow(),
                        BinaryExpression.Operator.valueOf(rs.getString("operator")),
                        readById(rs.getLong("right_id")).orElseThrow()
                )
        );
    }


    private Optional<ExpressionEntity> readById(Long id) {
        try {
            String sql = "SELECT type FROM expression WHERE id = :id";
            ExpressionType type = template.queryForObject(sql, Map.of("id", id), ExpressionType.class);

            return Optional.of(switch (type) {
                case STRING_CONDITION -> readStringCondition(id);
                case NUMBER_CONDITION -> readNumberCondition(id);
                case BINARY -> readBinary(id);
                case PREVIOUS_ORDINATION -> readPreviousOrdination(id);
            });
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
