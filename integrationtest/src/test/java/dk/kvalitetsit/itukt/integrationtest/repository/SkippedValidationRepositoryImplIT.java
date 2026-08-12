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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkippedValidationRepositoryImplIT extends BaseTest {
    private ClauseRepository clauseRepository;
    private SkippedValidationRepository skippedValidationRepository;

    @BeforeAll
    void setup() {
        clauseRepository = new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource()));
        skippedValidationRepository = new SkippedValidationRepositoryImpl(appDatabase.getDatasource());
    }

    @Test
    void create_WithEmptyList_DoesNotFail() {
        assertDoesNotThrow(() -> skippedValidationRepository.create(List.of()), "Creating skipped validations with an empty list should not fail");
    }

    @Test
    void createAndExists() {
        var condition = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "test");
        var clauseInput = new ClauseEntityInput("test", condition, "message", Clause.Status.DRAFT, "tester", null, null);
        var clause = clauseRepository.create(clauseInput);
        var createdSkippedValidation = new SkippedValidationEntity(clause.id(), "actor", "person");
        var uncreatedSkippedValidation = new SkippedValidationEntity(clause.id(), "actor", "another person");

        skippedValidationRepository.create(List.of(createdSkippedValidation));
        boolean createdEntityExists = skippedValidationRepository.exists(createdSkippedValidation);
        boolean uncreatedEntityExists = skippedValidationRepository.exists(uncreatedSkippedValidation);

        assertTrue(createdEntityExists, "Skipped validation should exist after creation");
        assertFalse(uncreatedEntityExists, "Skipped validation should not exist when not created");
    }

    @Test
    void create_SameEntityTwice_DoesNotFail() {
        var condition = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "test");
        var clauseInput = new ClauseEntityInput("test", condition, "message", Clause.Status.DRAFT, "tester", null, null);
        var clause = clauseRepository.create(clauseInput);
        var skippedValidation = new SkippedValidationEntity(clause.id(), "actor", "person");

        skippedValidationRepository.create(List.of(skippedValidation));
        assertDoesNotThrow(() -> skippedValidationRepository.create(List.of(skippedValidation)), "Creating the same skipped validation twice should not fail");
    }

    @Test
    void copySkippedValidation_givenASkippedValidation_whenCopySkippedValidation_thenEnsureItExist() {
        var condition = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "test");
        var clauseInput = new ClauseEntityInput("test", condition, "message", Clause.Status.ACTIVE, "tester", null, null);

        var clause1 = clauseRepository.create(clauseInput);
        var clause2 = clauseRepository.create(clauseInput);

        var skippedValidation = new SkippedValidationEntity(clause1.id(), "actor", "person");
        skippedValidationRepository.create(List.of(skippedValidation));


        assertDoesNotThrow(() -> skippedValidationRepository.copySkippedValidation(clause1.id(), clause2.id()));

        var skippedValidation1 = new SkippedValidationEntity(clause1.id(), "actor", "person");
        var skippedValidation2 = new SkippedValidationEntity(clause2.id(), "actor", "person");

        Assertions.assertTrue(skippedValidationRepository.exists(skippedValidation1));
        Assertions.assertTrue(skippedValidationRepository.exists(skippedValidation2));
    }

    @Test
    void deleteOlderThan_WithNoSkippedValidations_ReturnsZero() {
        long deletedCount = skippedValidationRepository.deleteOlderThan(Period.ofDays(1));

        assertEquals(0, deletedCount, "Expected no skipped validations to be deleted when none exist");
    }

    @Test
    void deleteOlderThan_WithNoSkippedValidationsOlderThanGivenPeriod_ReturnsZero() {
        var condition = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "test");
        var clauseInput = new ClauseEntityInput("test", condition, "message", Clause.Status.ACTIVE, "tester", null);
        var clause = clauseRepository.create(clauseInput);
        var skippedValidation = new SkippedValidationEntity(clause.id(), "actor", "person");
        skippedValidationRepository.create(List.of(skippedValidation));

        long deletedCount = skippedValidationRepository.deleteOlderThan(Period.ofDays(1));

        assertEquals(0, deletedCount, "Expected no skipped validations to be deleted when none are older than the given period");
    }

    @Test
    void deleteOlderThan_WithTwoSkippedValidationsOlderThanGivenPeriod_ReturnsTwo() {
        var condition = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "test");
        var clauseInput = new ClauseEntityInput("test", condition, "message", Clause.Status.ACTIVE, "tester", null);
        var clause1 = clauseRepository.create(clauseInput);
        var clause2 = clauseRepository.create(clauseInput);

        var skippedValidation1 = new SkippedValidationEntity(clause1.id(), "actor1", "person1");
        var skippedValidation2 = new SkippedValidationEntity(clause1.id(), "actor2", "person2");
        skippedValidationRepository.create(List.of(skippedValidation1, skippedValidation2));
        skippedValidationRepository.copySkippedValidation(clause1.id(), clause2.id());

        long deletedCount = skippedValidationRepository.deleteOlderThan(Period.ofDays(0));

        assertEquals(2, deletedCount, "Expected two skipped validations to be deleted when both are older than the given period");
        assertFalse(skippedValidationRepository.exists(skippedValidation1), "Expected the first skipped validation to be deleted");
        assertFalse(skippedValidationRepository.exists(skippedValidation2), "Expected the second skipped validation to be deleted");
    }
}