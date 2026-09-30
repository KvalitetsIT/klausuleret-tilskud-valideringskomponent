package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ErrorLoggingValidationServiceTest {
    @Mock
    private ValidationService<ValidationInput, List<ValidationError>> validationService;

    @Mock
    private Logger logger;

    @Test
    void validate_WithNoValidationErrors_DoesNotLog() {
        try (var ignored = Mockito.mockStatic(LoggerFactory.class)) {
            Mockito.when(LoggerFactory.getLogger(ErrorLoggingValidationService.class)).thenReturn(logger);
            Mockito.when(validationService.validate(Mockito.any(ValidationInput.class))).thenReturn(List.of());
            var errorLoggingValidationService = new ErrorLoggingValidationService(validationService);

            errorLoggingValidationService.validate(Mockito.mock(ValidationInput.class));

            Mockito.verifyNoInteractions(logger);
        }
    }

    @Test
    void validate_WithValidationErrors_LogsClauses() {
        try (var ignored = Mockito.mockStatic(LoggerFactory.class)) {
            Mockito.when(LoggerFactory.getLogger(ErrorLoggingValidationService.class)).thenReturn(logger);
            var validationError = new ValidationError(new ValidationError.Clause("1", "", ""), "", 0, "");
            Mockito.when(validationService.validate(Mockito.any(ValidationInput.class))).thenReturn(List.of(validationError));
            var errorLoggingValidationService = new ErrorLoggingValidationService(validationService);

            errorLoggingValidationService.validate(Mockito.mock(ValidationInput.class));

            Mockito.verify(logger, Mockito.times(1)).info("Validation failed for clauses: {}", Set.of("1"));
        }
    }
}