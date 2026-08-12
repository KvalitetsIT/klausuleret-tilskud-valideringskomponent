package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.common.repository.entity.SkippedValidationEntity;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepositoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class StartupIT extends BaseTest {
    private ClauseRepository clauseRepository;
    private SkippedValidationRepository skippedValidationRepository;

    @BeforeAll
    void setup() {
        clauseRepository = new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource()));
        skippedValidationRepository = new SkippedValidationRepositoryImpl(appDatabase.getDatasource());
    }

    @Test
    void atServiceStartup_DeletesSkippedValidationsOlderThanConfiguredDuration() {
        var condition = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "test");
        var clauseInput = new ClauseEntityInput("test", condition, "message", Clause.Status.ACTIVE, "tester", null);
        var clause = clauseRepository.create(clauseInput);
        var skippedValidation = new SkippedValidationEntity(clause.id(), "actor", "person");
        skippedValidationRepository.create(List.of(skippedValidation));

        restartService();

        boolean existsAfterRestart = skippedValidationRepository.exists(skippedValidation);
        assertFalse(existsAfterRestart, "Skipped validation should be deleted after service restart, because retention period is configured to 0 days");
    }
}
