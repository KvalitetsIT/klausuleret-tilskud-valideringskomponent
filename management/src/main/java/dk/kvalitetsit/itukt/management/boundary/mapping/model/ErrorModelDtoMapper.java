package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Error;

public class ErrorModelDtoMapper implements Mapper<dk.kvalitetsit.itukt.common.model.Error, Error> {
    @Override
    public Error map(dk.kvalitetsit.itukt.common.model.Error entry) {
        return new Error(entry.message(), entry.code());
    }
}
