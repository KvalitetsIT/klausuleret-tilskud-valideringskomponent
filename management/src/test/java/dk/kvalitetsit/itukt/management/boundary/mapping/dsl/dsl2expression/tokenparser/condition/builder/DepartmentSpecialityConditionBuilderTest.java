package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.DepartmentSpecialityCondition;
import org.openapitools.model.Operator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DepartmentSpecialityConditionBuilderTest {
    @InjectMocks
    private DepartmentSpecialityConditionBuilder departmentSpecialityConditionBuilder;

    @Test
    void build_WithNonEqualOperator_ThrowsException() {
        var value = new Condition.Value.Simple("someSpeciality");
        var operator = Operator.GREATER_THAN;

        var e = assertThrows(DslParserException.class, () -> departmentSpecialityConditionBuilder.build(operator, value));
        assertEquals("Unsupported operator for department speciality condition: >", e.getMessage());
    }

    @Test
    void build_WithEqualOperator_ReturnsDepartmentSpecialityCondition() {
        var value = new Condition.Value.Simple("someSpeciality");
        var operator = Operator.EQUAL;

        var condition = departmentSpecialityConditionBuilder.build(operator, value);

        var expectedCondition = new DepartmentSpecialityCondition(value.value(), ExpressionType.DEPARTMENT_SPECIALITY);
        assertEquals(expectedCondition, condition);
    }
}