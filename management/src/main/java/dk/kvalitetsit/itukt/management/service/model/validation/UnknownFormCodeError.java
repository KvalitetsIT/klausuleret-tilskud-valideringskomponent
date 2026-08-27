package dk.kvalitetsit.itukt.management.service.model.validation;

import dk.kvalitetsit.itukt.common.model.DrugMedication;

import java.util.Set;

public record UnknownFormCodeError(
        String formCode,
        Set<DrugMedication.Form> knownForms
) implements ExpressionValidationError {

    @Override
    public String errorMessage() {
        return "Unknown form code '%s'. Known form codes are: %s".formatted(
                formCode,
                knownForms.stream().map(DrugMedication.Form::code).sorted().toList());
    }
}
