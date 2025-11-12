package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ExistingDrugMedicationCondition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExistingDrugMedicationExpressionDslMapperImplTest {

    @InjectMocks
    private ExistingDrugMedicationExpressionDslMapperImpl mapper;

    @Test
    void merge_givenTwoValidExistingDrugMedicationConditions_whenMerge_thenCorrectlyMergeTheConditionsIntoOne() {
        var subject = List.of(
                new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("atcCode1").formCode("formCode1").routeOfAdministrationCode("routeOfAdministration1"),
                new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("atcCode2").formCode("formCode2").routeOfAdministrationCode("routeOfAdministration2")
        );
        var expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = atcCode1, FORM = formCode1, ROUTE = routeOfAdministration1}, {ATC = atcCode2, FORM = formCode2, ROUTE = routeOfAdministration2}]";
        var actual = this.mapper.merge(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);

    }

    @Test
    void map_givenAValidExistingDrugMedicationCondition_whenMap_thenReturnCorrectlyMappedDsl() {
        ExistingDrugMedicationCondition subject = new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("atcCode").formCode("formCode").routeOfAdministrationCode("routeOfAdministration");

        var expected = new Dsl("EKSISTERENDE_LÆGEMIDDEL = {ATC = atcCode, FORM = formCode, ROUTE = routeOfAdministration}", Dsl.Type.CONDITION);
        var actual = this.mapper.map(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithExistingDrugConditionIncludingWildcards_whenMap_thenParseDrugCorrectly() {
        final ExistingDrugMedicationCondition subject = new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("*").routeOfAdministrationCode("*");

        Dsl expected = new Dsl("EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}", Dsl.Type.CONDITION);
        Dsl actual = mapper.map(subject);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

}