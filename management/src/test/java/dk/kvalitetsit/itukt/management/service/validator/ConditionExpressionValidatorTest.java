package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownFormCodeError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConditionExpressionValidatorTest {

    @Mock
    private ExpressionValidatorFactory expressionValidatorFactory;

    @Mock
    private ExpressionValidator<DepartmentSpecialityConditionExpression> departmentSpecialityExpressionValidator;

    @Mock
    private ExpressionValidator<ExistingDrugMedicationConditionExpression> existingDrugMedicationValidator;

    private ConditionExpressionValidator validator;

    @BeforeEach
    void setUp() {
        when(expressionValidatorFactory.createDepartmentSpecialityExpressionValidator()).thenReturn(departmentSpecialityExpressionValidator);
        when(expressionValidatorFactory.createExistingDrugMedicationExpressionValidator()).thenReturn(existingDrugMedicationValidator);
        validator = new ConditionExpressionValidator(expressionValidatorFactory);
    }

    @Test
    void validate_WhenExpressionIsDepartmentSpeciality_DelegatesToDepartmentSpecialityValidator() {
        var condition = Mockito.mock(DepartmentSpecialityConditionExpression.class);
        var error = Mockito.mock(UnknownDepartmentSpecialityError.class);
        Mockito.when(departmentSpecialityExpressionValidator.validate(condition)).thenReturn(List.of(error));

        var result = validator.validate(condition);

        assertEquals(List.of(error), result);
    }

    @Test
    void validate_WhenExpressionIsExistingDrugMedication_DelegatesToExistingDrugMedicationValidator() {
        var condition = Mockito.mock(ExistingDrugMedicationConditionExpression.class);
        var error = Mockito.mock(UnknownFormCodeError.class);
        Mockito.when(existingDrugMedicationValidator.validate(condition)).thenReturn(List.of(error));

        var result = validator.validate(condition);

        assertEquals(List.of(error), result);
    }

    @Test
    void validate_WhenExpressionIsNotHandledByValidator_ReturnsNoErrors() {
        var result = validator.validate(Mockito.mock(AgeConditionExpression.class));

        assertEquals(List.of(), result);
        verifyNoInteractions(departmentSpecialityExpressionValidator);
    }
}

