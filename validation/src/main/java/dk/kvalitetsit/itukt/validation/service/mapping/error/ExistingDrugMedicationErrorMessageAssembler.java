package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExistingDrugMedicationErrorMessageAssembler implements ErrorMessageAssembler<ValidationError.ExistingDrugMedicationError> {
    private final StamdataCacheService<Medication.ATC> atcCache;
    private final StamdataCacheService<Medication.Form> formCache;

    public ExistingDrugMedicationErrorMessageAssembler(StamdataCacheService<Medication.ATC> atcCache, StamdataCacheService<Medication.Form> formCache) {
        this.atcCache = atcCache;
        this.formCache = formCache;
    }

    @Override
    public String assemble(ValidationError.ExistingDrugMedicationError error) {
        String requiredValues = Stream.of(
                        toErrorString("ATC", error.existingDrugMedication().atcCode(), code -> atcCache.get(code).map(Medication.ATC::text)),
                        toErrorString("Formkode", error.existingDrugMedication().formCode(), code -> formCache.get(code).map(Medication.Form::text)),
                        toErrorString("Administrationsvejkode", error.existingDrugMedication().routeOfAdministrationCode())
                )
                .flatMap(Optional::stream)
                .collect(Collectors.joining(", "));
        return "tidligere medicinsk behandling med følgende påkrævet: " + requiredValues;
    }

    private Optional<String> toErrorString(String key, String value, Function<String, Optional<String>> valueMapper) {
        return nonWildcard(value)
                .map(valueMapper)
                .map(mappedValue -> mappedValue
                        .map(mv -> "%s = '%s' (%s)".formatted(key, mv, value))
                        .orElse("%s = '%s'".formatted(key, value)));
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
