package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.ValidationError;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExistingDrugMedicationErrorMapper implements ValidationErrorMapper<ValidationError.ExistingDrugMedicationError> {
    @Override
    public String map(ValidationError.ExistingDrugMedicationError error) {
        String requiredValues = Stream.of(
                        toErrorString("ATC", error.existingDrugMedication().atcCode()),
                        toErrorString("Formkode", error.existingDrugMedication().formCode()),
                        toErrorString("Administrationsvejkode", error.existingDrugMedication().routeOfAdministrationCode())
                )
                .flatMap(Optional::stream)
                .collect(Collectors.joining(", "));
        return "tidligere medicinsk behandling med følgende påkrævet: " + requiredValues;
    }

    private Optional<String> toErrorString(String key, String value) {
        return value.equals(ExistingDrugMedicationConditionExpression.WILDCARD)
                ? Optional.empty()
                : Optional.of(key + " = " + value);
    }
}
