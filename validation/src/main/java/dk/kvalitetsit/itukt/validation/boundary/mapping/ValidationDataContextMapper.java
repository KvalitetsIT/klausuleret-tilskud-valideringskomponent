package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.ClauseField;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;

import java.util.List;
import java.util.Map;

public class ValidationDataContextMapper implements Mapper<ValidationInput, DataContext> {
    @Override
    public DataContext map(ValidationInput validationInput) {
        return new DataContext(Map.of(
                ClauseField.AGE.name(), List.of(Integer.toString(validationInput.citizenAge())))
        );
    }

}
