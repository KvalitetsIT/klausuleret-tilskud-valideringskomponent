package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.IndicationCondition;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class IndicationExpressionDslMapperImplTest {

    @InjectMocks
    private IndicationExpressionDslMapperImpl mapper;

    @Test
    void merge_givenDslWithTwoIndicationConditions_whenMap_thenMergeCorrectly() {
        var conditions = List.of(
                new IndicationCondition().type(ExpressionType.INDICATION).value("indication1"),
                new IndicationCondition().type(ExpressionType.INDICATION).value("indication2")
        );

        String expected = Identifier.INDICATION + " i [indication1, indication2]";
        String actual = mapper.merge(conditions);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + conditions);
    }

    @Test
    void map_givenAValidIndicationExpressionCondition_whenMap_thenReturnExpectedDsl() {
        var condition = new IndicationCondition().type(ExpressionType.INDICATION).value("blaah");
        var actual = mapper.map(condition);
        var expected = new Dsl(Identifier.INDICATION + " = blaah", Dsl.Type.CONDITION);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + condition);
    }
}