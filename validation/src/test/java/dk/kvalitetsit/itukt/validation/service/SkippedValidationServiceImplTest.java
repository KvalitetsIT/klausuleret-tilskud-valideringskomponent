package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkippedValidationServiceImplTest {
    @InjectMocks
    private SkippedValidationServiceImpl skippedValidationService;
    @Mock
    private SkippedValidationRepository skippedValidationRepository;
    @Mock
    private ClauseService clauseService;

    @Test
    void createSkippedValidations_WithNoClausesMatchingErrorCodes_CallsCreateWithEmptyList() {
        var errorCodes = List.of(1, 2, 3);
        Mockito.when(clauseService.getByErrorCode(Mockito.anyInt())).thenReturn(Optional.empty());

        skippedValidationService.createSkippedValidations("actor", "person", errorCodes);

        Mockito.verify(skippedValidationRepository).create(List.of());
    }

    @Test
    void createSkippedValidations_WithClausesMatchingErrorCodes_CreatesSkippedValidationsInRepository() {
        String actorId = "actor";
        String personId = "person";
        var errorCode1 = 1;
        var errorCode2 = 2;
        var errorCode3 = 3;
        var clause1 = Mockito.mock(Clause.Persisted.class);
        var clause2 = Mockito.mock(Clause.Persisted.class);
        Mockito.when(clause1.id()).thenReturn(10L);
        Mockito.when(clause2.id()).thenReturn(20L);
        Mockito.when(clauseService.getByErrorCode(1)).thenReturn(Optional.of(clause1));
        Mockito.when(clauseService.getByErrorCode(2)).thenReturn(Optional.of(clause2));

        skippedValidationService.createSkippedValidations(actorId, personId, List.of(errorCode1, errorCode2, errorCode3));

        var expectedSkippedValidation = List.of(
                new SkippedValidationEntity(clause1.id(), actorId, personId),
                new SkippedValidationEntity(clause2.id(), actorId, personId));
        Mockito.verify(skippedValidationRepository).create(Mockito.argThat(skippedValidations -> {
                    assertEquals(expectedSkippedValidation.size(), skippedValidations.size(), "Number of created skipped validations should match number of clause IDs");
                    assertTrue(skippedValidations.containsAll(expectedSkippedValidation), "Created skipped validations should match input values and clause IDs");
                    return true;
                }
        ));
    }

    @Test
    void shouldSkipValidation_WhenSkippedValidationDoesNotExistInRepository_ReturnsFalse() {
        String actorId = "actor";
        String personId = "person";
        long clauseId = 10L;
        Mockito.when(skippedValidationRepository.exists(new SkippedValidationEntity(clauseId, actorId, personId))).thenReturn(false);

        boolean shouldSkipValidation = skippedValidationService.shouldSkipValidation(actorId, personId, clauseId);

        assertFalse(shouldSkipValidation, "shouldSkipValidation should return false when skipped validation does not exist in repository");
    }

    @Test
    void shouldSkipValidation_WhenSkippedValidationExistsInRepository_ReturnsTrue() {
        String actorId = "actor";
        String personId = "person";
        long clauseId = 20L;
        Mockito.when(skippedValidationRepository.exists(new SkippedValidationEntity(clauseId, actorId, personId))).thenReturn(true);

        boolean shouldSkipValidation = skippedValidationService.shouldSkipValidation(actorId, personId, clauseId);

        assertTrue(shouldSkipValidation, "shouldSkipValidation should return true when skipped validation exists in repository");
    }
}