package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExistingDrugMedicationErrorMessageAssembler implements ErrorMessageAssembler<ValidationError.ExistingDrugMedicationError> {
    private final StamdataCacheService<Medication.ATC> atcCache;

    public ExistingDrugMedicationErrorMessageAssembler(StamdataCacheService<Medication.ATC> atcCache) {
        this.atcCache = atcCache;
    }

    @Override
    public String assemble(ValidationError.ExistingDrugMedicationError error) {
        String requiredValues = Stream.of(
                        toAtcErrorString(error.existingDrugMedication().atcCode()),
                        toErrorString("Formkode", error.existingDrugMedication().formCode()),
                        toErrorString("Administrationsvejkode", error.existingDrugMedication().routeOfAdministrationCode())
                )
                .flatMap(Optional::stream)
                .collect(Collectors.joining(", "));
        return "tidligere medicinsk behandling med følgende påkrævet: " + requiredValues;
    }

    private Optional<String> toAtcErrorString(String atcCode) {
        return nonWildcard(atcCode).map(code -> atcCache.get(code)
                .map(atc -> "ATC = '%s' (%s)".formatted(atc.text(), code))
                .orElse("ATC = '%s'".formatted(code)));
    }

    private Optional<String> toErrorString(String key, String value) {
        return nonWildcard(value).map(v -> "%s = '%s'".formatted(key, v));
    }

    private Optional<String> nonWildcard(String value) {
        return value.equals(ExistingDrugMedicationConditionExpression.WILDCARD)
                ? Optional.empty()
                : Optional.of(value);
    }
}
