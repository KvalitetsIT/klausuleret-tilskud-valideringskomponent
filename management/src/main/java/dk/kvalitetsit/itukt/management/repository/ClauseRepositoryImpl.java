package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.util.*;

public class ClauseRepositoryImpl implements ClauseRepository<ClauseEntity> {

    private static final Logger logger = LoggerFactory.getLogger(ClauseRepositoryImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ClauseRepositoryImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<ClauseEntity> create(ClauseEntity entry) throws ServiceException {
        try {
            UUID uuid = UUID.randomUUID();
            KeyHolder keyHolder = new GeneratedKeyHolder();

            template.update(
                    "INSERT INTO clause (uuid, name) VALUES (:uuid, :name)",
                    new MapSqlParameterSource()
                            .addValue("uuid", uuid.toString())
                            .addValue("name", entry.name()),
                    keyHolder,
                    new String[]{"id"}
            );

            long clauseId = Optional.ofNullable(keyHolder.getKey())
                    .orElseThrow(() -> new ServiceException("Failed to generate clause primary key"))
                    .longValue();

            // Create expression with fresh expression ID = clause ID
            ExpressionEntity expr = create(clauseId, entry.expression());

            return Optional.of(new ClauseEntity(clauseId, uuid, entry.name(), expr));

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
        }
    }

    private ExpressionEntity create(long clauseId, ExpressionEntity expression) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Use provided ID directly, do not use GeneratedKeyHolder here
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
            case ExpressionEntity.ConditionEntity e -> insertCondition(expressionId, e);
            case ExpressionEntity.BinaryExpressionEntity e -> insertBinary(clauseId, expressionId, e);
            case ExpressionEntity.ParenthesizedExpressionEntity e -> insertParenthesized(clauseId, expressionId, e);
        };

        // Return a copy with the correct ID
//        return expression.withId(expressionId); // Make sure ExpressionEntity has `withId(long)` method
    }

    @Override
    public Optional<ClauseEntity> read(UUID uuid) throws ServiceException {
        try {
            String sql = """
                        SELECT c.id, c.name, e.type
                        FROM clause c
                        JOIN expression e ON c.id = e.id
                        WHERE c.uuid = :uuid
                    """;

            Map<String, Object> row = template.queryForMap(sql, Map.of("uuid", uuid.toString()));

            Long id = ((Number) row.get("id")).longValue();
            String name = (String) row.get("name");
            ExpressionType type = ExpressionType.valueOf((String) row.get("type"));

            ExpressionEntity expression = switch (type) {
                case ExpressionType.CONDITION -> readCondition(id);
                case ExpressionType.BINARY -> readBinary(id);
                case ExpressionType.PARENTHESIZED -> readParenthesized(id);
            };

            return Optional.of(new ClauseEntity(id, uuid, name, expression));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to read expression {}", uuid, e);
            throw new ServiceException("Failed to read expression", e);
        }
    }


    @Override
    public List<ClauseEntity> readAll() throws ServiceException {
        try {
            String sql = """
                        SELECT c.uuid
                        FROM clause c
                        JOIN expression e ON c.id = e.id
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

    private ExpressionEntity.ConditionEntity insertCondition(long parentId, ExpressionEntity.ConditionEntity condition) {

        template.update(
                "INSERT INTO condition_expression(expression_id, field, operator) VALUES (:expression_id, :field, :operator)",
                Map.of(
                        "expression_id", parentId,
                        "field", condition.field(),
                        "operator", condition.operator().getValue()
                ));

        for (String value : condition.values()) {
            template.update(
                    "INSERT INTO condition_value(condition_id, value) VALUES (:condition_id, :value)",
                    Map.of(
                            "condition_id", parentId,
                            "value", value
                    ));
        }
        return condition.withId(parentId);
    }

    // Note the new 'clauseId' parameter
    private ExpressionEntity.BinaryExpressionEntity insertBinary(long clauseId, long parentId, ExpressionEntity.BinaryExpressionEntity binary) {
        // Pass the original clauseId, not the parent expression's ID
        ExpressionEntity left = create(clauseId, binary.left());
        ExpressionEntity right = create(clauseId, binary.right());

        template.update(
                "INSERT INTO binary_expression(expression_id, left_id, operator, right_id) VALUES (:expression_id, :left_id, :operator, :right_id)",
                Map.of(
                        "expression_id", parentId,
                        "left_id", left.id(),
                        "operator", binary.operator().toString(),
                        "right_id", right.id()
                ));
        return new ExpressionEntity.BinaryExpressionEntity(parentId, left, binary.operator(), right);
    }

    // Note the new 'clauseId' parameter
    private ExpressionEntity.ParenthesizedExpressionEntity insertParenthesized(long clauseId, long parentId, ExpressionEntity.ParenthesizedExpressionEntity paren) {
        // Pass the original clauseId
        var inner = create(clauseId, paren.inner());

        template.update(
                "INSERT INTO parenthesized_expression(expression_id, inner_id) VALUES (:expression_id, :inner_id)",
                Map.of(
                        "expression_id", parentId,
                        "inner_id", inner.id()
                ));

        return new ExpressionEntity.ParenthesizedExpressionEntity(parentId, inner);
    }

    private ExpressionEntity.ConditionEntity readCondition(long expressionId) {
        var cond = template.queryForObject(
                "SELECT field, operator FROM condition_expression WHERE expression_id = :id",
                Map.of("id", expressionId),
                (rs, rowNum) -> new Object[]{rs.getString("field"), rs.getString("operator")}
        );

        List<String> values = template.query(
                "SELECT value FROM condition_value WHERE condition_id = :id ORDER BY id",
                Map.of("id", expressionId),
                (rs, rowNum) -> rs.getString("value"));

        return new ExpressionEntity.ConditionEntity(expressionId, (String) cond[0], Operator.fromValue((String) cond[1]), values);
    }


    private ExpressionEntity readBinary(Long id) {
        return template.queryForObject(
                "SELECT left_id, operator, right_id FROM binary_expression WHERE expression_id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new ExpressionEntity.BinaryExpressionEntity(
                        id,
                        readById(rs.getLong("left_id")).orElseThrow(),
                        Expression.BinaryExpression.BinaryOperator.valueOf(rs.getString("operator")),
                        readById(rs.getLong("right_id")).orElseThrow()
                )
        );
    }

    private ExpressionEntity readParenthesized(Long id) {
        Long innerId = template.queryForObject(
                "SELECT inner_id FROM parenthesized_expression WHERE expression_id = :id",
                Map.of("id", id),
                Long.class
        );

        ExpressionEntity inner = readById(innerId).orElseThrow();
        return new ExpressionEntity.ParenthesizedExpressionEntity(innerId, inner);
    }


    private Optional<ExpressionEntity> readById(Long id) {
        try {
            String sql = "SELECT type FROM expression WHERE id = :id";
            ExpressionType type = template.queryForObject(sql, Map.of("id", id), ExpressionType.class);

            return Optional.of(switch (type) {
                case CONDITION -> readCondition(id);
                case BINARY -> readBinary(id);
                case PARENTHESIZED -> readParenthesized(id);
            });
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
