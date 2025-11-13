package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.IndicationCondition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ExpressionDtoDslMapperTest {

    private ExpressionDtoDslMapper mapper;

    @Mock
    private MapperFactory factory;

    @Mock
    private ExpressionDslMapper<BinaryExpression> binaryExpressionExpressionDslMapper;

    @Mock
    private ExpressionDslMapper<AgeCondition> ageConditionExpressionDslMapper;

    @Mock
    private ExpressionDslMapper<IndicationCondition> indicationConditionExpressionDslMapper;

    @Mock
    private ExpressionDslMapper<ExistingDrugMedicationCondition> existingDrugMedicationConditionExpressionDslMapper;

    @BeforeEach
    void setup() {
        Mockito.when(factory.getBinaryExpressionExpressionDslMapper(Mockito.any())).thenReturn(binaryExpressionExpressionDslMapper);
        Mockito.when(factory.getAgeConditionExpressionDslMapper()).thenReturn(ageConditionExpressionDslMapper);
        Mockito.when(factory.getIndicationConditionExpressionDslMapper()).thenReturn(indicationConditionExpressionDslMapper);
        Mockito.when(factory.getExistingDrugMedicationConditionExpressionDslMapper()).thenReturn(existingDrugMedicationConditionExpressionDslMapper);
        mapper = new ExpressionDtoDslMapper(factory);
    }

    @Test
    void givenABinaryExpression_whenMap_thenInvokeCorrectMapper() {
        BinaryExpression subject = mock(BinaryExpression.class);
        Mockito.when(binaryExpressionExpressionDslMapper.map(subject)).thenReturn(mock(Dsl.class));
        this.mapper.map(subject);
        Mockito.verify(binaryExpressionExpressionDslMapper, Mockito.times(1)).map(subject);
    }

    @Test
    void givenAnExistingDrugMedicationConditionExpression_whenMap_thenInvokeCorrectMapper() {
        ExistingDrugMedicationCondition subject = mock(ExistingDrugMedicationCondition.class);
        Mockito.when(existingDrugMedicationConditionExpressionDslMapper.map(subject)).thenReturn(mock(Dsl.class));
        this.mapper.map(subject);
        Mockito.verify(existingDrugMedicationConditionExpressionDslMapper, Mockito.times(1)).map(subject);
    }

    @Test
    void givenAnAgeCondition_whenMap_thenInvokeCorrectMapper() {
        AgeCondition subject = mock(AgeCondition.class);
        Mockito.when(ageConditionExpressionDslMapper.map(subject)).thenReturn(mock(Dsl.class));
        this.mapper.map(subject);
        Mockito.verify(ageConditionExpressionDslMapper, Mockito.times(1)).map(subject);
    }

    @Test
    void givenAnIndicationCondition_whenMap_thenInvokeCorrectMapper() {
        IndicationCondition subject = mock(IndicationCondition.class);
        Mockito.when(indicationConditionExpressionDslMapper.map(subject)).thenReturn(mock(Dsl.class));
        this.mapper.map(subject);
        Mockito.verify(indicationConditionExpressionDslMapper, Mockito.times(1)).map(subject);
    }


    @Test
    void givenASingleCondition_whenMergeConditions_thenCallMapForSingleCondition() {
        AgeCondition single = mock(AgeCondition.class);
        ExpressionDtoDslMapper spyMapper = Mockito.spy(mapper);
        doReturn("blaah").when(spyMapper).map(single);

        String result = spyMapper.mergeConditions(List.of(single));

        Assertions.assertEquals("blaah", result);
        verify(spyMapper).map(single);
        verifyNoInteractions(ageConditionExpressionDslMapper, indicationConditionExpressionDslMapper, existingDrugMedicationConditionExpressionDslMapper);
    }

    @Test
    void givenTwoAgeConditions_whenMergeConditions_thenInvokeCorrespondingMapper() {
        AgeCondition c1 = mock(AgeCondition.class);
        AgeCondition c2 = mock(AgeCondition.class);
        when(ageConditionExpressionDslMapper.merge(anyList())).thenReturn("blaah");

        String result = mapper.mergeConditions(List.of(c1, c2));

        Assertions.assertEquals("blaah", result);
        verify(ageConditionExpressionDslMapper).merge(anyList());
        verifyNoInteractions(indicationConditionExpressionDslMapper, existingDrugMedicationConditionExpressionDslMapper);
    }

    @Test
    void givenTwoIndicationConditions_whenMergeConditions_thenInvokeCorrespondingMapper() {
        IndicationCondition c1 = mock(IndicationCondition.class);
        IndicationCondition c2 = mock(IndicationCondition.class);
        when(indicationConditionExpressionDslMapper.merge(anyList())).thenReturn("blaah");

        String result = mapper.mergeConditions(List.of(c1, c2));

        Assertions.assertEquals("blaah", result);
        verify(indicationConditionExpressionDslMapper).merge(anyList());
        verifyNoInteractions(ageConditionExpressionDslMapper, existingDrugMedicationConditionExpressionDslMapper);
    }

    @Test
    void givenTwoExistingDrugMedicationConditions_whenMergeConditions_thenInvokeCorrespondingMapper() {
        ExistingDrugMedicationCondition c1 = mock(ExistingDrugMedicationCondition.class);
        ExistingDrugMedicationCondition c2 = mock(ExistingDrugMedicationCondition.class);
        when(existingDrugMedicationConditionExpressionDslMapper.merge(anyList())).thenReturn("blaah");

        String result = mapper.mergeConditions(List.of(c1, c2));

        Assertions.assertEquals("blaah", result);
        verify(existingDrugMedicationConditionExpressionDslMapper).merge(anyList());
        verifyNoInteractions(ageConditionExpressionDslMapper, indicationConditionExpressionDslMapper);
    }

    @Test
    void givenTwoBinaryExpressions_whenMergeConditions_thenThrow() {
        BinaryExpression binary = mock(BinaryExpression.class);
        assertThrows(IllegalStateException.class, () -> mapper.mergeConditions(List.of(binary, binary)));
    }


}