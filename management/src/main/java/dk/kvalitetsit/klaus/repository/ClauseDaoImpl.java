package dk.kvalitetsit.klaus.repository;


import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.ClauseEntity;
import dk.kvalitetsit.klaus.model.ExpressionEntity;
import dk.kvalitetsit.klaus.model.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class ClauseDaoImpl implements ClauseDao {

    private static final Logger logger = LoggerFactory.getLogger(ClauseDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ClauseDaoImpl(@Qualifier("validationDataSource") DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<ClauseEntity> create(ClauseEntity entry) throws ServiceException {
        try {
            // 1. Insert into clause to get ID and UUID
            UUID uuid = UUID.randomUUID();
            KeyHolder keyHolder = new GeneratedKeyHolder();

            MapSqlParameterSource clauseParams = new MapSqlParameterSource()
                    .addValue("uuid", uuid.toString())
                    .addValue("name", entry.name());

            template.update(
                    "INSERT INTO clause (uuid, name) VALUES (:uuid, :name)",
                    clauseParams,
                    keyHolder,
                    new String[]{"id"}
            );

            Number generatedId = Optional.ofNullable(keyHolder.getKey()).orElseThrow(() -> new ServiceException("Failed to generate clause ID"));

            long id = generatedId.longValue();

            MapSqlParameterSource exprParams = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("type", entry.expression().type());

            template.update("INSERT INTO expression (id, type) VALUES (:id, :type)", exprParams);

            ExpressionEntity expr = entry.expression();
            switch (expr.type()) {
                case "condition_expression" -> insertCondition(id, (ExpressionEntity.ConditionEntity) expr);
                case "binary_expression" -> insertBinary(id, (ExpressionEntity.BinaryExpressionEntity) expr);
                case "parenthesized_expression" ->
                        insertParenthesized(id, (ExpressionEntity.ParenthesizedExpressionEntity) expr);
                default -> throw new IllegalStateException("Unknown expression type: " + expr.type());
            }

            return Optional.of(new ClauseEntity(id, uuid, entry.name(), expr));

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
        }
    }

    @Override
    public List<ClauseEntity> create(List<ClauseEntity> entry) throws ServiceException {
        return entry.stream().map(this::create).filter(Optional::isPresent).map(Optional::get).toList();
    }

    @Override
    public Optional<ClauseEntity> delete(UUID uuid) throws ServiceException {
        try {
            Optional<ClauseEntity> existing = read(uuid);
            if (existing.isEmpty()) return Optional.empty();

            template.update("DELETE FROM clause WHERE uuid = :uuid", Map.of("uuid", uuid.toString()));
            return existing;
        } catch (Exception e) {
            logger.error("Failed to delete clause {}", uuid, e);
            throw new ServiceException("Failed to delete clause", e);
        }
    }

    @Override
    public Optional<ClauseEntity> read(UUID uuid) throws ServiceException {
        try {
            String sql = "SELECT id, type FROM clause WHERE uuid = :uuid";
            Map<String, Object> row = template.queryForMap(sql, Map.of("uuid", uuid.toString()));

            Long id = ((Number) row.get("id")).longValue();
            String name = (String) row.get("name");
            String type = (String) row.get("type");

            ExpressionEntity expression = switch (type) {
                case "condition_expression" -> readCondition(id);
                case "binary_expression" -> readBinary(id);
                case "parenthesized_expression" -> readParenthesized(id);
                default -> throw new IllegalStateException("Unknown expression type: " + type);
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
    public List<ClauseEntity> read_all(Pagination pagination) throws ServiceException {
        try {
            String sql = "SELECT id FROM expression ORDER BY id LIMIT :limit OFFSET :offset";
            List<UUID> ids = template.query(sql, Map.of(
                    "limit", pagination.limit(),
                    "offset", pagination.offset()
            ), (rs, rowNum) -> UUID.fromString(rs.getString("id")));

            return ids.stream().map(this::read).filter(Optional::isPresent).map(Optional::get).toList();
        } catch (Exception e) {
            logger.error("Failed to read all expressions with pagination", e);
            throw new ServiceException("Failed to read expressions", e);
        }
    }


    @Override
    public List<ClauseEntity> read_all() throws ServiceException {
        try {
            String sql = "SELECT id FROM expression";
            List<UUID> ids = template.query(sql, Collections.emptyMap(), (rs, rowNum) ->
                    UUID.fromString(rs.getString("id"))
            );

            return ids.stream().map(this::read).map(Optional::get).toList();
        } catch (Exception e) {
            logger.error("Failed to read all expressions", e);
            throw new ServiceException("Failed to read expressions", e);
        }
    }


    @Override
    public Optional<ClauseEntity> update(UUID id, ClauseEntity entry) throws ServiceException {
        try {
            delete(id);
            return create(entry);
        } catch (Exception e) {
            logger.error("Failed to update expression {}", id, e);
            throw new ServiceException("Failed to update expression", e);
        }
    }


    private void insertCondition(long expressionId, ExpressionEntity.ConditionEntity condition) {
        var params = Map.of(
                "expression_id", expressionId,
                "field", condition.field(),
                "operator", condition.operator()
        );

        template.update("""
                INSERT INTO condition_expression(expression_id, field, operator) 
                VALUES (:expression_id, :field, :operator)
                """, params);

        for (String value : condition.values()) {
            var valueParams = Map.of(
                    "condition_id", expressionId,
                    "value", value
            );
            template.update("""
                    INSERT INTO condition_value(condition_id, value)
                    VALUES (:condition_id, :value)
                    """, valueParams);
        }
    }

    private void insertBinary(long expressionId, ExpressionEntity.BinaryExpressionEntity binary) {
        ClauseEntity left = create(new ClauseEntity(0L, UUID.randomUUID(), "", binary.left())).orElseThrow();
        ClauseEntity right = create(new ClauseEntity(0L, UUID.randomUUID(), "", binary.right())).orElseThrow();

        var params = Map.of(
                "expression_id", expressionId,
                "left_id", left.id(),
                "operator", binary.operator(),
                "right_id", right.id()
        );

        template.update("""
                INSERT INTO binary_expression(expression_id, left_id, operator, right_id)
                VALUES (:expression_id, :left_id, :operator, :right_id)
                """, params);
    }

    private void insertParenthesized(long expressionId, ExpressionEntity.ParenthesizedExpressionEntity paren) {
        var inner = create(new ClauseEntity(0L, UUID.randomUUID(), "", paren.inner())).orElseThrow();

        var params = Map.of(
                "expression_id", expressionId,
                "inner_id", inner.id()
        );

        template.update("""
                INSERT INTO parenthesized_expression(expression_id, inner_id)
                VALUES (:expression_id, :inner_id)
                """, params);
    }

    private ExpressionEntity.ConditionEntity readCondition(long expressionId) {
        var cond = template.queryForObject("""
                    SELECT field, operator FROM condition_expression WHERE expression_id = :id
                """, Map.of("id", expressionId), (rs, rowNum) ->
                new Object[]{rs.getString("field"), rs.getString("operator")}
        );

        List<String> values = template.query("""
                    SELECT value FROM condition_value WHERE condition_id = :id ORDER BY id
                """, Map.of("id", expressionId), (rs, rowNum) -> rs.getString("value"));

        return new ExpressionEntity.ConditionEntity((String) cond[0], (String) cond[1], values);
    }


    private ExpressionEntity readBinary(Long id) {
        var row = template.queryForObject("""
                    SELECT left_id, operator, right_id FROM binary_expression WHERE expression_id = :id
                """, Map.of("id", id), (rs, rowNum) ->
                new Object[]{
                        rs.getLong("left_id"),
                        rs.getString("operator"),
                        rs.getLong("right_id")
                }
        );

        ExpressionEntity left = readById((Long) row[0]).orElseThrow();
        ExpressionEntity right = readById((Long) row[2]).orElseThrow();

        return new ExpressionEntity.BinaryExpressionEntity(left, (String) row[1], right);
    }

    private ExpressionEntity readParenthesized(Long id) {
        Long innerId = template.queryForObject(
                "SELECT inner_id FROM parenthesized_expression WHERE expression_id = :id",
                Map.of("id", id),
                Long.class
        );

        ExpressionEntity inner = readById(innerId).orElseThrow();
        return new ExpressionEntity.ParenthesizedExpressionEntity(inner);
    }


    private Optional<ExpressionEntity> readById(Long id) {
        try {
            String sql = "SELECT type FROM expression WHERE id = :id";
            String type = template.queryForObject(sql, Map.of("id", id), String.class);

            return Optional.of(switch (type) {
                case "condition_expression" -> readCondition(id);
                case "binary_expression" -> readBinary(id);
                case "parenthesized_expression" -> readParenthesized(id);
                default -> throw new IllegalStateException("Unknown expression type: " + type);
            });
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
