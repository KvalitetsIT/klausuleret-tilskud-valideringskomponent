package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

public class SkippedValidationRepositoryImpl implements SkippedValidationRepository {
    private final NamedParameterJdbcTemplate template;

    public SkippedValidationRepositoryImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void create(List<SkippedValidationEntity> skippedValidations) {
        if (skippedValidations.isEmpty()) return;
        String sql = """
                INSERT INTO skipped_validation (clause_id, actor_id, person_id)
                VALUES (:clauseId, :actorId, :personId)
                ON DUPLICATE KEY UPDATE clause_id = clause_id
                """;
        var batchParams = skippedValidations.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        template.batchUpdate(sql, batchParams);
    }

    @Override
    public boolean exists(SkippedValidationEntity skippedValidation) {
        String sql = """
            SELECT EXISTS(
                SELECT 1 FROM skipped_validation
                WHERE clause_id = :clauseId AND actor_id = :actorId AND person_id = :personId
            )
            """;
        Boolean exists = template.queryForObject(sql, new BeanPropertySqlParameterSource(skippedValidation), Boolean.class);
        return Boolean.TRUE.equals(exists);
    }
}
