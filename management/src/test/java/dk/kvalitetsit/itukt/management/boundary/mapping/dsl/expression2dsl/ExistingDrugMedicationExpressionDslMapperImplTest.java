package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ExistingDrugMedicationCondition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExistingDrugMedicationExpressionDslMapperImplTest {
    @Mock
    private StringToDslValueMapper stringToDslValueMapper;

    @InjectMocks
    private ExistingDrugMedicationExpressionDslMapperImpl mapper;

    @Test
    void merge_givenTwoValidExistingDrugMedicationConditions_whenMerge_thenCorrectlyMergeTheConditionsIntoOne() {
        String atcCode1 = "atcCode1";
        String formCode1 = "formCode1";
        String route1 = "route1";
        String atcCode2 = "atcCode2";
        String formCode2 = "formCode2";
        String route2 = "route2";
        var subject = List.of(
                new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode(atcCode1).formCode(formCode1).routeOfAdministrationCode(route1),
                new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode(atcCode2).formCode(formCode2).routeOfAdministrationCode(route2)
        );
        String mappedAtcCode1 = "mappedAtcCode1";
        String mappedFormCode1 = "mappedFormCode1";
        String mappedRoute1 = "mappedRoute1";
        String mappedAtcCode2 = "mappedAtcCode2";
        String mappedFormCode2 = "mappedFormCode2";
        String mappedRoute2 = "mappedRoute2";

        Mockito.when(stringToDslValueMapper.map(atcCode1)).thenReturn(mappedAtcCode1);
        Mockito.when(stringToDslValueMapper.map(formCode1)).thenReturn(mappedFormCode1);
        Mockito.when(stringToDslValueMapper.map(route1)).thenReturn(mappedRoute1);
        Mockito.when(stringToDslValueMapper.map(atcCode2)).thenReturn(mappedAtcCode2);
        Mockito.when(stringToDslValueMapper.map(formCode2)).thenReturn(mappedFormCode2);
        Mockito.when(stringToDslValueMapper.map(route2)).thenReturn(mappedRoute2);

        var expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = mappedAtcCode1, FORM = mappedFormCode1, ROUTE = mappedRoute1}, {ATC = mappedAtcCode2, FORM = mappedFormCode2, ROUTE = mappedRoute2}]";
        var actual = this.mapper.merge(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);

    }

    @Test
    void map_givenAValidExistingDrugMedicationCondition_whenMap_thenReturnCorrectlyMappedDsl() {
        String atcCode = "atcCode";
        String formCode = "formCode";
        String route = "routeOfAdministration";
        ExistingDrugMedicationCondition subject = new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode(atcCode).formCode(formCode).routeOfAdministrationCode(route);
        String mappedAtcCode = "mappedAtcCode";
        String mappedFormCode = "mappedFormCode";
        String mappedRoute = "mappedRoute";
        Mockito.when(stringToDslValueMapper.map(atcCode)).thenReturn(mappedAtcCode);
        Mockito.when(stringToDslValueMapper.map(formCode)).thenReturn(mappedFormCode);
        Mockito.when(stringToDslValueMapper.map(route)).thenReturn(mappedRoute);

        var expected = new Dsl("EKSISTERENDE_LÆGEMIDDEL = {ATC = mappedAtcCode, FORM = mappedFormCode, ROUTE = mappedRoute}", Dsl.Type.CONDITION);
        var actual = this.mapper.map(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }
}