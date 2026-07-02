package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedValueException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.DoctorSpecialityCondition;
import org.openapitools.model.Operator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DoctorSpecialityConditionBuilderTest {
    @InjectMocks
    private DoctorSpecialityConditionBuilder doctorSpecialityConditionBuilder;

    @Test
    void build_WithNonEqualOperator_ThrowsException() {
        var value = new Condition.Value.Simple("someSpeciality");
        var operator = Operator.GREATER_THAN;

        var e = assertThrows(UnexpectedValueException.class, () -> doctorSpecialityConditionBuilder.build(operator, value));
        assertEquals(operator.getValue(), e.getValue());
    }

    @Test
    void build_WithEqualOperator_ReturnsDoctorSpecialityCondition() throws DslParserException {
        var value = new Condition.Value.Simple("someSpeciality");
        var operator = Operator.EQUAL;

        var condition = doctorSpecialityConditionBuilder.build(operator, value);

        var expectedCondition = new DoctorSpecialityCondition(value.value(), ExpressionType.DOCTOR_SPECIALITY);
        assertEquals(expectedCondition, condition);
    }
}