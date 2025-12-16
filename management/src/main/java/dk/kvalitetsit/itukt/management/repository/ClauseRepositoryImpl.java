package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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

            String sql = "INSERT INTO clause (uuid, name, expression_id, error_message) " +
                    "VALUES (:uuid, :name, :expression_id, :error_message) " +
                    "RETURNING id, created_time";

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("uuid", uuid.toString())
                    .addValue("name", clause.name())
                    .addValue("expression_id", createdExpression.id())
                    .addValue("error_message", clause.errorMessage());


            Map<String, Object> result = template.queryForMap(sql, params);

            Long clauseId = ((Number) result.get("id")).longValue();

            Instant createdAt = ((Timestamp) result.get("created_time")).toLocalDateTime().toInstant(ZoneOffset.UTC);

            int errorCode = createOrGetErrorCode(clause.name());

            return new ClauseEntity(clauseId, uuid, clause.name(), errorCode, clause.errorMessage(), createdExpression, Date.from(createdAt));

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
                                Date.from(rs.getTimestamp("created_time", Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toInstant())
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
    public boolean nameExists(String name) throws ServiceException {
        String sql = "SELECT COUNT(*) FROM clause WHERE name = :name";
        Integer count = template.queryForObject(
                sql,
                Map.of("name", name),
                Integer.class);
        return count != null && count > 0;
    }


    @Override
    public List<ClauseEntity> readAll() throws ServiceException {
        try {
            String sql = """
                        SELECT c.uuid
                        FROM clause c
                        JOIN (
                            SELECT name, MAX(created_time) AS max_created_time
                            FROM clause
                            GROUP BY name
                        ) latest
                          ON c.name = latest.name
                            AND c.created_time = latest.max_created_time
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
            logger.error("Failed to read all clauses", e);
            throw new ServiceException("Failed to read clauses", e);
        }
    }

    @Override
    public List<ClauseEntity> readHistory(String name) {
        try {
            String sql = """
                        SELECT c.*
                        FROM clause c
                        WHERE c.name = :name
                        ORDER BY c.created_time
                    """;

            List<UUID> uuids = template.query(sql, Map.of("name", name), (rs, rowNum) ->
                    UUID.fromString(rs.getString("uuid"))
            );

            return uuids.stream()
                    .map(this::read)
                    .flatMap(Optional::stream)
                    .toList();

        } catch (Exception e) {
            logger.error("Failed to read all clauses", e);
            throw new ServiceException("Failed to read clauses", e);
        }

    }


}
