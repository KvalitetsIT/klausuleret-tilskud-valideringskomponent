package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.Operator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AgeConditionDslMapperImplTest {
    @InjectMocks
    private AgeConditionDslMapperImpl mapper;


    @Test
    void map_givenAValidAgeCondition_whenMap_thenReturnExpectedDsl() {
        var condition = new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10);
        var actual = mapper.map(condition);
        var expected = new Dsl(Identifier.AGE + " = 10", Dsl.Type.CONDITION);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + condition);
    }

    @Test
    void merge_givenDslWithTwoAgeConditions_whenMap_thenMergeCorrectly() {
        var conditions = List.of(
                new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10),
                new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20)
        );

        String expected = Identifier.AGE + " i [10, 20]";
        String actual = mapper.merge(conditions);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + conditions);
    }
}