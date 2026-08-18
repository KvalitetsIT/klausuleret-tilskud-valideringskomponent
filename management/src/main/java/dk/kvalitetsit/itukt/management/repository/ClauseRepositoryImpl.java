package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseQuery;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.*;

public class ClauseRepositoryImpl implements ClauseRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClauseRepositoryImpl.class);
    private static final int MIN_ERROR_CODE = 10800;
    private static final int MAX_ERROR_CODE = 10999;
    private final NamedParameterJdbcTemplate template;
    private final ExpressionRepository expressionRepository;

    public ClauseRepositoryImpl(DataSource dataSource, ExpressionRepository expressionRepository) {
        template = new NamedParameterJdbcTemplate(dataSource);
        this.expressionRepository = expressionRepository;
    }

    @Override
    public ClauseEntity create(ClauseEntityInput clauseInput) {
        try {
            UUID uuid = UUID.randomUUID();

            ExpressionEntity createdExpression = expressionRepository.create(clauseInput.expression());

            String sql = "INSERT INTO clause (uuid, name, expression_id, error_message, status, created_by, primary_parent_id, secondary_parent_id) " +
                    "VALUES (:uuid, :name, :expression_id, :error_message, :status, :created_by, :primary_parent_id, :secondary_parent_id) " +
                    "RETURNING id, created_time";

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("uuid", uuid.toString())
                    .addValue("name", clauseInput.name())
                    .addValue("expression_id", createdExpression.id())
                    .addValue("error_message", clauseInput.errorMessage())
                    .addValue("status", clauseInput.status().name())
                    .addValue("created_by", clauseInput.createdBy())
                    .addValue("primary_parent_id", clauseInput.primaryParentId())
                    .addValue("secondary_parent_id", clauseInput.secondaryParentId());


            return template.queryForObject(sql, params, (rs, rowNum) -> {

                int errorCode = createOrGetErrorCode(clauseInput.name());

                return new ClauseEntity(
                        rs.getLong("id"),
                        uuid,
                        clauseInput.name(),
                        clauseInput.status(),
                        errorCode,
                        clauseInput.errorMessage(),
                        createdExpression,
                        clauseInput.createdBy(),
                        rs.getTimestamp("created_time"),
                        clauseInput.primaryParentId(),
                        clauseInput.secondaryParentId()
                );
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to create clause", e);
        }
    }

    private int createOrGetErrorCode(String clauseName) {
        var existingErrorCodes = template.queryForList(
                "SELECT error_code FROM error_code WHERE clause_name = :clause_name",
                Map.of("clause_name", clauseName),
                Integer.class
        );
        return existingErrorCodes.isEmpty() ? createErrorCode(clauseName)
                : existingErrorCodes.getFirst();
    }

    private synchronized int createErrorCode(String clauseName) {
        Integer max = template.getJdbcTemplate().queryForObject(
                "SELECT MAX(error_code) FROM error_code",
                Integer.class
        );

        if (max != null && max < MIN_ERROR_CODE) {
            throw new IllegalStateException("An error code was found in the database that is below the allowed range (%d-%d exhausted). Found: %d"
                    .formatted(MIN_ERROR_CODE, MAX_ERROR_CODE, max));
        }

        int next = Optional.ofNullable(max).map(m -> m + 1).orElse(MIN_ERROR_CODE);

        if (next > MAX_ERROR_CODE) {
            throw new IllegalStateException("Exceeded the maximum number of allocated error codes (%d–%d exhausted)"
                    .formatted(MIN_ERROR_CODE, MAX_ERROR_CODE));
        }

        template.update(
                "INSERT INTO error_code (error_code, clause_name) VALUES (:error_code, :clause_name)",
                new MapSqlParameterSource()
                        .addValue("error_code", next)
                        .addValue("clause_name", clauseName)
        );
        return next;
    }

    @Override
    public Optional<ClauseEntity> read(UUID uuid) {
        try {
            String sql = """
                        SELECT c.id, c.name, c.status, c.expression_id, error_code.error_code, c.error_message, c.created_by, c.created_time, c.primary_parent_id, c.secondary_parent_id
                        FROM clause c
                        JOIN error_code ON c.name = error_code.clause_name
                        WHERE c.uuid = :uuid
                    """;

            var clause = template.queryForObject(
                    sql,
                    Map.of("uuid", uuid.toString()),
                    (rs, rowNum) -> {
                        long expressionId = rs.getLong("expression_id");
                        var expression = expressionRepository.read(expressionId).orElseThrow(() -> new RuntimeException(String.format("Expected to find an expression with id '%s', but nothing was found", expressionId)));

                        return new ClauseEntity(
                                rs.getLong("id"),
                                uuid,
                                rs.getString("name"),
                                Clause.Status.valueOf(rs.getString("status")),
                                rs.getInt("error_code"),
                                rs.getString("error_message"),
                                expression,
                                rs.getString("created_by"),
                                rs.getTimestamp("created_time"),
                                rs.getObject("primary_parent_id", Long.class),
                                rs.getObject("secondary_parent_id", Long.class)
                        );
                    });

            return Optional.ofNullable(clause);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read clause %s".formatted(uuid), e);
        }
    }

    @Override
    public List<ClauseEntity> read(ClauseQuery query) {
        try {
            StringBuilder sql = new StringBuilder("""
                    SELECT c.uuid
                    FROM clause c
                    WHERE 1=1
                    """);

            Map<String, Object> params = new HashMap<>();

            query.getName().ifPresent(name -> {
                sql.append(" AND c.name = :name");
                params.put("name", name);
            });

            if (!query.getStatuses().isEmpty()) {
                sql.append(" AND c.status IN (:statuses)");
                params.put("statuses", query.getStatuses().stream().map(Clause.Status::name).toList());
            }

            if (query.isWithoutChildren()) {
                sql.append("""
                        AND NOT EXISTS (
                            SELECT 1
                            FROM clause child
                            WHERE child.primary_parent_id = c.id OR child.secondary_parent_id = c.id
                        )
                    """);
            }

            if (query.isWithoutPrimaryChildren()) {
                sql.append("""
                        AND NOT EXISTS (
                            SELECT 1
                            FROM clause child
                            WHERE child.primary_parent_id = c.id
                        )
                    """);
            }

            List<UUID> uuids = template.queryForList(
                    sql.toString(),
                    params,
                    UUID.class
            );

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to read clauses with query %s".formatted(query), e);
        }
    }

    @Override
    public ClauseEntity deleteDraft(UUID id) throws NotFoundException {
        var clause = read(id).filter(c -> c.status() == Clause.Status.DRAFT)
                .orElseThrow(() -> new NotFoundException("No clause found with uuid %s and status DRAFT".formatted(id)));
        template.update(
                "DELETE FROM clause WHERE uuid = :uuid",
                Map.of("uuid", id.toString())
        );
        expressionRepository.delete(clause.expression().id());
        return clause;

    }

    @Override
    public Optional<ClauseEntity> readParent(UUID uuid) {
        try {
            var sql = """
                    SELECT parent.uuid
                    FROM clause
                    LEFT JOIN clause parent ON clause.primary_parent_id = parent.id
                    WHERE clause.uuid = :uuid
                    """;
            UUID parentUuid = template.queryForObject(sql, Map.of("uuid", uuid.toString()), UUID.class);
            return Optional.ofNullable(parentUuid).flatMap(this::read);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
