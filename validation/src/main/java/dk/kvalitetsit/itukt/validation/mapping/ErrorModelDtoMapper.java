package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Error;

public class ErrorModelDtoMapper implements Mapper<Error, org.openapitools.model.Error> {
    @Override
    public org.openapitools.model.Error map(Error entry) {
        return new org.openapitools.model.Error(entry.message(), entry.code());
    }
}
