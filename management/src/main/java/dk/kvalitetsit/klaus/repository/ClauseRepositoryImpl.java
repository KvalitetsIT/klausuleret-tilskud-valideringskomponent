package dk.kvalitetsit.klaus.repository;


import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Operator;
import dk.kvalitetsit.klaus.model.Pagination;
import dk.kvalitetsit.klaus.repository.model.ClauseEntity;
import dk.kvalitetsit.klaus.repository.model.ExpressionEntity;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Repository
public class ClauseRepositoryImpl implements ClauseRepository<ClauseEntity> {

    private static final Logger logger = LoggerFactory.getLogger(ClauseRepositoryImpl.class);
    private final NamedParameterJdbcTemplate template;
    private final ExecutorService clauseCreatorExecutor = Executors.newFixedThreadPool(10);

    public ClauseRepositoryImpl(@Qualifier("validationDataSource") DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<ClauseEntity> create(ClauseEntity entry) throws ServiceException {
        try {
            UUID uuid = UUID.randomUUID();
            KeyHolder keyHolder = new GeneratedKeyHolder();

            template.update(
                    "INSERT INTO clause (uuid, version, name) VALUES (:uuid, :version, :name)",
                    new MapSqlParameterSource()
                            .addValue("uuid", uuid.toString())
                            .addValue("version", entry.version())
                            .addValue("name", entry.name()),
                    keyHolder,
                    new String[]{"id"}
            );

            long clauseId = Optional.ofNullable(keyHolder.getKey())
                    .orElseThrow(() -> new ServiceException("Failed to generate clause primary key"))
                    .longValue();

            // Create expression with fresh expression ID = clause ID
            ExpressionEntity expr = create(clauseId, entry.expression());

            return Optional.of(new ClauseEntity(clauseId, uuid, entry.name(), entry.version(), expr));

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
        }
    }

    private ExpressionEntity create(long clauseId, ExpressionEntity expression) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Use provided ID directly, do not use GeneratedKeyHolder here
        template.update(
                "INSERT INTO expression (clause_id, type) VALUES (:clause_id, :type)",
                new MapSqlParameterSource()
                        .addValue("clause_id", clauseId)
                        .addValue("type", expression.type()),
                keyHolder,
                new String[]{"id"}
        );

        long expressionId = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new ServiceException("Failed to get expression primary key"))
                .longValue();

        switch (expression.type()) {
            case "condition_expression" -> insertCondition(
                    expressionId,
                    (ExpressionEntity.ConditionEntity) expression
            );
            case "binary_expression" -> insertBinary(
                    clauseId,
                    expressionId,
                    (ExpressionEntity.BinaryExpressionEntity) expression
            );
            case "parenthesized_expression" -> insertParenthesized(
                    clauseId,
                    expressionId,
                    (ExpressionEntity.ParenthesizedExpressionEntity) expression
            );
        }

        // Return a copy with the correct ID
        return expression.withId(expressionId); // Make sure ExpressionEntity has `withId(long)` method
    }

    @Override
    public List<ClauseEntity> create(List<ClauseEntity> entry) throws ServiceException {
        var version = fetchNextVersion();
        // Assign the version to all entries
        var entries = entry.stream().map(e -> new ClauseEntity(e.id(), e.uuid(), e.name(), version, e.expression())).toList();
        // Create a list of async tasks
        List<CompletableFuture<Optional<ClauseEntity>>> futures = entries.stream()
                .map(entity -> CompletableFuture.supplyAsync(() -> this.create(entity), clauseCreatorExecutor))
                .toList();

        // Wait for all the futures to complete and collect the results
        return futures.stream()
                .map(CompletableFuture::join) // .join() waits for completion and gets the result
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public Optional<ClauseEntity> delete(UUID id) throws ServiceException {
        try {
            Optional<ClauseEntity> existing = read(id);
            if (existing.isEmpty()) return Optional.empty();

            template.update("DELETE FROM clause WHERE uuid = :uuid", Map.of("uuid", id.toString()));
            return existing;
        } catch (Exception e) {
            logger.error("Failed to delete clause {}", id, e);
            throw new ServiceException("Failed to delete clause", e);
        }
    }

    @Override
    public Optional<ClauseEntity> read(UUID uuid) throws ServiceException {
        try {
            String sql = """
                        SELECT c.id, c.name, e.type, c.version
                        FROM clause c
                        JOIN expression e ON c.id = e.id
                        WHERE c.uuid = :uuid
                    """;

            Map<String, Object> row = template.queryForMap(sql, Map.of("uuid", uuid.toString()));

            Long id = ((Number) row.get("id")).longValue();
            String name = (String) row.get("name");
            String type = (String) row.get("type");
            Integer version = (Integer) row.get("version");

            ExpressionEntity expression = switch (type) {
                case "condition_expression" -> readCondition(id);
                case "binary_expression" -> readBinary(id);
                case "parenthesized_expression" -> readParenthesized(id);
                default -> throw new IllegalStateException("Unknown expression type: " + type);
            };

            return Optional.of(new ClauseEntity(id, uuid, name, version, expression));

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
            String sql = """
                        SELECT c.uuid
                        FROM clause c
                        JOIN expression e ON c.id = e.id
                        ORDER BY c.id
                        LIMIT :limit OFFSET :offset
                    """;

            List<UUID> uuids = template.query(sql, Map.of(
                    "limit", pagination.limit().orElse(0),
                    "offset", pagination.offset().orElse(10)
            ), (rs, rowNum) -> UUID.fromString(rs.getString("uuid")));

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            logger.error("Failed to read all expressions with pagination", e);
            throw new ServiceException("Failed to read expressions", e);
        }
    }


    @Override
    public List<ClauseEntity> read_all() throws ServiceException {
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


    private void insertCondition(long parentId, ExpressionEntity.ConditionEntity condition) {

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
    }

    // Note the new 'clauseId' parameter
    private void insertBinary(long clauseId, long parentId, ExpressionEntity.BinaryExpressionEntity binary) {
        // Pass the original clauseId, not the parent expression's ID
        ExpressionEntity left = create(clauseId, binary.left());
        ExpressionEntity right = create(clauseId, binary.right());

        template.update(
                "INSERT INTO binary_expression(expression_id, left_id, operator, right_id) VALUES (:expression_id, :left_id, :operator, :right_id)",
                Map.of(
                        "expression_id", parentId,
                        "left_id", left.id(),
                        "operator", binary.operator(),
                        "right_id", right.id()
                ));
    }

    // Note the new 'clauseId' parameter
    private void insertParenthesized(long clauseId, long parentId, ExpressionEntity.ParenthesizedExpressionEntity paren) {
        // Pass the original clauseId
        var inner = create(clauseId, paren.inner());

        template.update(
                "INSERT INTO parenthesized_expression(expression_id, inner_id) VALUES (:expression_id, :inner_id)",
                Map.of(
                        "expression_id", parentId,
                        "inner_id", inner.id()
                ));
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
                        rs.getString("operator"),
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

    private int fetchNextVersion() {
        String sql = "SELECT NEXT VALUE FOR clause_version_seq";
        return template.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }


}
