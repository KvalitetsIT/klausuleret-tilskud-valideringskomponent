package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepositoryImpl;
import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkippedValidationRepositoryImplIT extends BaseTest {
    private ClauseRepository clauseRepository;
    private SkippedValidationRepository skippedValidationRepository;

    @BeforeAll
    void setup() {
        clauseRepository = new ClauseRepositoryImpl(appDatabase.getDatasource());
        skippedValidationRepository = new SkippedValidationRepositoryImpl(appDatabase.getDatasource());
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }

    @Test
    void create_WithEmptyList_DoesNotFail() {
        assertDoesNotThrow(() -> skippedValidationRepository.create(List.of()));
    }

    @Test
    void createAndExists() {
        var condition = new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "test");
        var clause = clauseRepository.create(new ClauseForCreation("test", condition));
        var createdSkippedValidation = new SkippedValidationEntity(clause.id(), "actor", "person");
        var uncreatedSkippedValidation = new SkippedValidationEntity(clause.id(), "actor", "another person");

        skippedValidationRepository.create(List.of(createdSkippedValidation));
        boolean createdEntityExists = skippedValidationRepository.exists(createdSkippedValidation);
        boolean uncreatedEntityExists = skippedValidationRepository.exists(uncreatedSkippedValidation);

        assertTrue(createdEntityExists, "Skipped validation should exist after creation");
        assertFalse(uncreatedEntityExists, "Skipped validation should not exist when not created");
    }
}
