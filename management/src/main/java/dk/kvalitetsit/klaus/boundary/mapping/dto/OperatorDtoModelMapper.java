package dk.kvalitetsit.klaus.boundary.mapping.dto;

import dk.kvalitetsit.klaus.Mapper;
import org.openapitools.model.Operator;

public class OperatorDtoModelMapper implements Mapper<Operator, dk.kvalitetsit.klaus.model.Operator>  {
    @Override
    public dk.kvalitetsit.klaus.model.Operator map(Operator entry) {
        return switch (entry){
            case EQUAL -> dk.kvalitetsit.klaus.model.Operator.EQUAL;
            case I -> dk.kvalitetsit.klaus.model.Operator.IN;
            case GREATER_THAN_OR_EQUAL_TO -> dk.kvalitetsit.klaus.model.Operator.GREATER_THAN_OR_EQUAL_TO;
            case LESS_THAN_OR_EQUAL_TO -> dk.kvalitetsit.klaus.model.Operator.LESS_THAN_OR_EQUAL_TO;
            case GREATER_THAN -> dk.kvalitetsit.klaus.model.Operator.GREATER_THAN;
            case LESS_THAN -> dk.kvalitetsit.klaus.model.Operator.LESS_THAN;
        };
    }
}