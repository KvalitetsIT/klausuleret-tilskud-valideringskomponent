package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public interface ErrorMessageAssembler<T extends ValidationError> {
    String assemble(T error);
}
