package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryImpl implements ClauseRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClauseRepositoryImpl.class);
    private final NamedParameterJdbcTemplate template;
    private final ExpressionRepository expressionRepository;

    public ClauseRepositoryImpl(DataSource dataSource, ExpressionRepository expressionRepository) {
        template = new NamedParameterJdbcTemplate(dataSource);
        this.expressionRepository = expressionRepository;
    }

    @Override
    public ClauseEntity create(ClauseInput clause) throws ServiceException {
        try {
            UUID uuid = UUID.randomUUID();

            ExpressionEntity createdExpression = expressionRepository.create(clause.expression());

            String sql = "INSERT INTO clause (uuid, name, expression_id, error_message, status) " +
                    "VALUES (:uuid, :name, :expression_id, :error_message, :status) " +
                    "RETURNING id, created_time";

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("uuid", uuid.toString())
                    .addValue("name", clause.name())
                    .addValue("expression_id", createdExpression.id())
                    .addValue("error_message", clause.errorMessage())
                    .addValue("status", Clause.Status.DRAFT.name());


            return template.queryForObject(sql, params, (rs, rowNum) -> {

                int errorCode = createOrGetErrorCode(clause.name());

                return new ClauseEntity(
                        rs.getLong("id"),
                        uuid,
                        clause.name(),
                        errorCode,
                        clause.errorMessage(),
                        createdExpression,
                        rs.getTimestamp("created_time")
                );
            });

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
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
                "SELECT COALESCE(MAX(error_code), 10799) FROM error_code",
                Integer.class
        );

        int next = max + 1;

        if (next > 10999) {
            throw new IllegalStateException("Exceeded the maximum number of allocated error codes (10800–10999 exhausted)");
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
    public Optional<ClauseEntity> read(UUID uuid) throws ServiceException {
        try {
            String sql = """
                        SELECT c.id, c.name, c.expression_id, error_code.error_code, c.error_message, c.created_time
                        FROM clause c
                        JOIN error_code ON c.name = error_code.clause_name
                        WHERE c.uuid = :uuid
                    """;

            var clause = template.queryForObject(
                    sql,
                    Map.of("uuid", uuid.toString()),
                    (rs, rowNum) -> {
                        long expressionId = rs.getLong("expression_id");
                        var expression = expressionRepository.read(expressionId).orElseThrow(() -> new ServiceException(String.format("Expected to find an expression with id '%s', but nothing was found", expressionId)));

                        return new ClauseEntity(
                                rs.getLong("id"),
                                uuid,
                                rs.getString("name"),
                                rs.getInt("error_code"),
                                rs.getString("error_message"),
                                expression,
                                rs.getTimestamp("created_time")
                        );
                    });

            return Optional.ofNullable(clause);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to read clause {}", uuid, e);
            throw new ServiceException("Failed to read clause", e);
        }
    }

    @Override
    public List<ClauseEntity> readAllActive() throws ServiceException {
        try {
            String sql = """
                        SELECT c.uuid
                        FROM clause c
                        JOIN (
                            SELECT name, MAX(created_time) AS max_created_time
                            FROM clause
                            WHERE status = :status
                            GROUP BY name
                        ) latest
                          ON c.name = latest.name
                            AND c.created_time = latest.max_created_time
                        ORDER BY c.id
                    """;

            List<UUID> uuids = template.query(sql,
                    Map.of("status", Clause.Status.ACTIVE.name()),
                    (rs, rowNum) -> UUID.fromString(rs.getString("uuid"))
            );

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            logger.error("Failed to read all clauses", e);
            throw new ServiceException("Failed to read active clauses", e);
        }
    }

    @Override
    public List<ClauseEntity> readAllDrafts() throws ServiceException {
        try {
            String sql = """
                        SELECT c.uuid
                        FROM clause c
                        WHERE status = :status
                        ORDER BY c.id
                    """;

            List<UUID> uuids = template.query(sql,
                    Map.of("status", Clause.Status.DRAFT.name()),
                    (rs, rowNum) -> UUID.fromString(rs.getString("uuid"))
            );

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            logger.error("Failed to read all clauses", e);
            throw new ServiceException("Failed to read draft clauses", e);
        }
    }

    @Override
    public List<ClauseEntity> readHistory(String name) {
        try {
            String sql = """
                        SELECT uuid
                        FROM clause
                        WHERE name = :name
                        ORDER BY created_time
                    """;

            List<UUID> uuids = template.queryForList(sql, Map.of("name", name), UUID.class);

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            var message = String.format("Failed to read the history of clause '%s'", name);
            logger.error(message, e);
            throw new ServiceException(message, e);
        }

    }

    @Override
    public void updateDraftToActive(UUID uuid) throws NotFoundException {
        String sql = """
                UPDATE clause
                SET status = :new_status
                WHERE uuid = :uuid AND status = :current_status
                """;

        int rowsAffected = template.update(
                sql,
                Map.of("uuid", uuid.toString(),
                        "current_status", Clause.Status.DRAFT.name(),
                        "new_status", Clause.Status.ACTIVE.name()));

        if (rowsAffected == 0) {
            throw new NotFoundException("No clause found with uuid %s in DRAFT status".formatted(uuid));
        }
    }


}
