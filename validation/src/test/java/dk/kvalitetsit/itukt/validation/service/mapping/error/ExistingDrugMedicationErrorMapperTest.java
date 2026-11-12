package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExistingDrugMedicationErrorMapperTest {
    private final ExistingDrugMedicationErrorMapper errorMapper = new ExistingDrugMedicationErrorMapper();


    @Test
    void map_ReturnsCorrespondingErrorString() {
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = errorMapper.map(conditionError);

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: ATC = atc, Formkode = form, Administrationsvejkode = adm", errorString);
    }

    @Test
    void map_WithAtcAndRouteWildcards_ReturnsCorrespondingErrorString() {
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication(ExistingDrugMedicationConditionExpression.WILDCARD, "form", ExistingDrugMedicationConditionExpression.WILDCARD);
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = errorMapper.map(conditionError);

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: Formkode = form", errorString);
    }

    @Test
    void map_WithFormWildcard_ReturnsCorrespondingErrorString() {
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", ExistingDrugMedicationConditionExpression.WILDCARD, "adm");
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = errorMapper.map(conditionError);

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: ATC = atc, Administrationsvejkode = adm", errorString);
    }
}