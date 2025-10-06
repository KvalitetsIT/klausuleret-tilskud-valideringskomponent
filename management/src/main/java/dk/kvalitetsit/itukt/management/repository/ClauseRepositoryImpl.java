package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
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
    public ClauseEntity create(ClauseForCreation clause) throws ServiceException {
        try {
            UUID uuid = UUID.randomUUID();
            KeyHolder keyHolder = new GeneratedKeyHolder();

            ExpressionEntity createdExpression = expressionRepository.create(clause.expression());

            template.update(
                    "INSERT INTO clause (uuid, name, expression_id) VALUES (:uuid, :name, :expression_id)",
                    new MapSqlParameterSource()
                            .addValue("uuid", uuid.toString())
                            .addValue("name", clause.name())
                            .addValue("expression_id", createdExpression.id()),
                    keyHolder,
                    new String[]{"id"}
            );

            long clauseId = Optional.ofNullable(keyHolder.getKey())
                    .orElseThrow(() -> new ServiceException("Failed to generate clause primary key"))
                    .longValue();

            int errorCode = createErrorCode(clause.name());

            return new ClauseEntity(clauseId, uuid, clause.name(), errorCode, createdExpression);

        } catch (Exception e) {
            logger.error("Failed to create clause", e);
            throw new ServiceException("Failed to create clause", e);
        }
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
                        SELECT c.id, c.name, c.expression_id, error_code.error_code
                        FROM clause c
                        JOIN error_code ON c.name = error_code.clause_name
                        WHERE c.uuid = :uuid
                    """;

            var clause = template.queryForObject(
                    sql,
                    Map.of("uuid", uuid.toString()),
                    (rs, rowNum) -> {
                        long expressionId = rs.getLong("expression_id");
                        var expression = expressionRepository.read(expressionId).orElseThrow();

                        return new ClauseEntity(
                                rs.getLong("id"),
                                uuid,
                                rs.getString("name"),
                                rs.getInt("error_code"),
                                expression
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
            logger.error("Failed to read all clauses", e);
            throw new ServiceException("Failed to read clauses", e);
        }
    }


}
