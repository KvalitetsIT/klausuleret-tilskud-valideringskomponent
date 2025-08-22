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
    public Optional<ClauseEntity> create(ClauseEntity clause) throws ServiceException {
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

            return Optional.of(new ClauseEntity(clauseId, uuid, clause.name(), expression));

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
        }
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
            case ExpressionEntity.ConditionEntity e -> insertCondition(e);
            case ExpressionEntity.BinaryExpressionEntity e -> insertBinary(e);
            case ExpressionEntity.ParenthesizedExpressionEntity e -> insertParenthesized(e);
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
            Long expression_id = ((Number) row.get("expression_id")).longValue();

            ExpressionEntity expression = switch (type) {
                case ExpressionType.CONDITION -> readCondition(expression_id);
                case ExpressionType.BINARY -> readBinary(expression_id);
                case ExpressionType.PARENTHESIZED -> readParenthesized(expression_id);
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

    private ExpressionEntity.ConditionEntity insertCondition(ExpressionEntity.ConditionEntity condition) {

        template.update(
                "INSERT INTO condition_expression(expression_id, field, operator) VALUES (:expression_id, :field, :operator)",
                Map.of(
                        "expression_id", condition.id(),
                        "field", condition.field(),
                        "operator", condition.operator().getValue()
                ));

        for (String value : condition.values()) {
            template.update(
                    "INSERT INTO condition_value(condition_id, value) VALUES (:condition_id, :value)",
                    Map.of(
                            "condition_id", condition.id(),
                            "value", value
                    ));
        }
        return condition;
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


    private ExpressionEntity.ParenthesizedExpressionEntity insertParenthesized(ExpressionEntity.ParenthesizedExpressionEntity paren) {
        var inner = create(paren.inner());

        template.update(
                "INSERT INTO parenthesized_expression(expression_id, inner_id) VALUES (:expression_id, :inner_id)",
                Map.of(
                        "expression_id", paren.id(),
                        "inner_id", inner.id()
                ));

        return new ExpressionEntity.ParenthesizedExpressionEntity(paren.id(), inner);
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
        return new ExpressionEntity.ParenthesizedExpressionEntity(id, inner);
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
