package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.service.CommonClauseService;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkippedValidationServiceImplTest {
    @InjectMocks
    private SkippedValidationServiceImpl skippedValidationService;
    @Mock
    private SkippedValidationRepository skippedValidationRepository;
    @Mock
    private CommonClauseService clauseService;

    @Test
    void createSkippedValidations_WithNoClausesMatchingErrorCodes_CallsCreateWithEmptyList() {
        var errorCodes = List.of(1, 2, 3);
        Mockito.when(clauseService.getClauseIdsByErrorCodes(errorCodes)).thenReturn(List.of());

        skippedValidationService.createSkippedValidations("actor", "person", errorCodes);

        Mockito.verify(skippedValidationRepository).create(List.of());
    }

    @Test
    void createSkippedValidations_WithClausesMatchingErrorCodes_CreatesSkippedValidationsInRepository() {
        String actorId = "actor";
        String personId = "person";
        var errorCodes = List.of(1, 2, 3);
        long clauseId1 = 10L;
        long clauseId2 = 20L;
        Mockito.when(clauseService.getClauseIdsByErrorCodes(errorCodes)).thenReturn(List.of(clauseId1, clauseId2));

        skippedValidationService.createSkippedValidations(actorId, personId, errorCodes);

        var expectedSkippedValidation = List.of(
                new SkippedValidationEntity(clauseId1, actorId, personId),
                new SkippedValidationEntity(clauseId2, actorId, personId));
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