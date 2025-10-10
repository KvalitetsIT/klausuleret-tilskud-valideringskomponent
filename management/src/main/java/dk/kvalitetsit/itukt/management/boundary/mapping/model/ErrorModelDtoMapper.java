package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import org.openapitools.model.Error;

public class ErrorModelDtoMapper implements Mapper<Clause.Error, Error> {
    @Override
    public Error map(Clause.Error entry) {
        return new Error(entry.message());
    }
}
