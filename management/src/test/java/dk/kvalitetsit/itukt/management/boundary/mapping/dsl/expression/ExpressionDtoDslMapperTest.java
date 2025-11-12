package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;


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

import static org.mockito.Mockito.mock;


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

}