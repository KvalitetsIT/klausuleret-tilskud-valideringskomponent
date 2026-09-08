package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.IndicationCondition;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SimpleConditionExpressionDslMapperImplTest {
    @Mock
    private StringToDslValueMapper stringToDslValueMapper;

    private SimpleConditionExpressionDslMapper<IndicationCondition> mapper;

    @BeforeEach
    void setUp() {
        mapper = new SimpleConditionExpressionDslMapper<>(Identifier.INDICATION, stringToDslValueMapper, IndicationCondition::getValue);
    }

    @Test
    void merge_givenDslWithTwoIndicationConditions_whenMap_thenMergeCorrectly() {
        String value1 = "indication1";
        String value2 = "indication2";
        var conditions = List.of(
                new IndicationCondition().type(ExpressionType.INDICATION).value(value1),
                new IndicationCondition().type(ExpressionType.INDICATION).value(value2)
        );
        String mappedValue1 = "mappedIndication1";
        String mappedValue2 = "mappedIndication2";
        Mockito.when(stringToDslValueMapper.map(value1)).thenReturn(mappedValue1);
        Mockito.when(stringToDslValueMapper.map(value2)).thenReturn(mappedValue2);

        String expected = Identifier.INDICATION + " i [mappedIndication1, mappedIndication2]";
        String actual = mapper.merge(conditions);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + conditions);
    }

    @Test
    void map_givenAValidIndicationExpressionCondition_whenMap_thenReturnExpectedDsl() {
        var condition = new IndicationCondition().type(ExpressionType.INDICATION).value("val");
        Mockito.when(stringToDslValueMapper.map(condition.getValue())).thenReturn("mapped-value");

        var actual = mapper.map(condition);

        var expected = new Dsl(Identifier.INDICATION + " = mapped-value", Dsl.Type.CONDITION);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + condition);
    }
}