package dk.kvalitetsit.itukt.boundary.mapping.dto;

import dk.kvalitetsit.itukt.Mapper;
import org.openapitools.model.Operator;

public class OperatorDtoModelMapper implements Mapper<Operator, dk.kvalitetsit.itukt.model.Operator>  {
    @Override
    public dk.kvalitetsit.itukt.model.Operator map(Operator entry) {
        return switch (entry){
            case EQUAL -> dk.kvalitetsit.itukt.model.Operator.EQUAL;
            case I -> dk.kvalitetsit.itukt.model.Operator.IN;
            case GREATER_THAN_OR_EQUAL_TO -> dk.kvalitetsit.itukt.model.Operator.GREATER_THAN_OR_EQUAL_TO;
            case LESS_THAN_OR_EQUAL_TO -> dk.kvalitetsit.itukt.model.Operator.LESS_THAN_OR_EQUAL_TO;
            case GREATER_THAN -> dk.kvalitetsit.itukt.model.Operator.GREATER_THAN;
            case LESS_THAN -> dk.kvalitetsit.itukt.model.Operator.LESS_THAN;
        };
    }
}