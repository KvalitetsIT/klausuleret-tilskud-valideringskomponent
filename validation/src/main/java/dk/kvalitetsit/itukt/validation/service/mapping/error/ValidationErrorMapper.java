package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.ValidationError;

public interface ValidationErrorMapper<T extends ValidationError> extends Mapper<T, String> {
}
