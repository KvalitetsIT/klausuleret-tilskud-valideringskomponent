package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import org.openapitools.model.Actor;
import org.openapitools.model.ExistingDrugMedicationInput;
import org.openapitools.model.ValidationRequest;

import java.util.List;
import java.util.Optional;

public class ValidationRequestInputMapper implements Mapper<ValidationRequest, List<ValidationInput>> {

    private final Mapper<Actor, ValidationInput.Actor> actorMapper;

    public ValidationRequestInputMapper(Mapper<Actor, ValidationInput.Actor> actorMapper) {
        this.actorMapper = actorMapper;
    }

    @Override
    public List<ValidationInput> map(ValidationRequest validationRequest) {
        return validationRequest.getValidate().stream().map(validate -> {
            var existingDrugMedication = Optional.ofNullable(validationRequest.getExistingDrugMedications().orElse(null))
                    .map(e -> e.stream()
                            .map(this::mapExistingDrugMedication)
                            .toList()
                    );

            var createdBy = actorMapper.map(validate.getNewDrugMedication().getCreatedBy());
            var reportedBy = validate.getNewDrugMedication().getReportedBy().map(actorMapper::map);

            return new ValidationInput(
                    validationRequest.getPersonIdentifier(),
                    createdBy,
                    reportedBy,
                    validationRequest.getSkipValidations(),
                    validationRequest.getAge(),
                    validate.getNewDrugMedication().getDrugIdentifier(),
                    validate.getNewDrugMedication().getIndicationCode(),
                    existingDrugMedication,
                    validate.getElementPath()
            );
        }).toList();

    }

    private dk.kvalitetsit.itukt.common.model.ExistingDrugMedication mapExistingDrugMedication(ExistingDrugMedicationInput existing) {
        return new dk.kvalitetsit.itukt.common.model.ExistingDrugMedication(existing.getAtcCode(), existing.getFormCode(), existing.getRouteOfAdministrationCode());
    }
}
