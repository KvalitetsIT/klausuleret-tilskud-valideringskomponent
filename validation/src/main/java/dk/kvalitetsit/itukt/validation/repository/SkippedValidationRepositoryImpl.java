package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

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
                """;

        var batchParams = skippedValidations.stream()
                .map(this::toMap)
                .toArray(Map[]::new);

        template.batchUpdate(sql, batchParams);
    }

    @Override
    public boolean exists(SkippedValidationEntity skippedValidation) {
        String sql = """
                SELECT COUNT(*) FROM skipped_validation
                WHERE clause_id = :clauseId AND actor_id = :actorId AND person_id = :personId
                """;

        Integer count = template.queryForObject(sql, toMap(skippedValidation), Integer.class);
        return count != null && count > 0;
    }

    private Map<String, ?> toMap(SkippedValidationEntity skippedValidation) {
        return Map.of(
                "clauseId", skippedValidation.clauseId(),
                "actorId", skippedValidation.actorId(),
                "personId", skippedValidation.personId()
        );
    }
}
