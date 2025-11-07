package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Operator;

class OperatorDtoModelMapper implements Mapper<Operator, dk.kvalitetsit.itukt.common.model.Operator>  {
    @Override
    public dk.kvalitetsit.itukt.common.model.Operator map(Operator entry) {
        return switch (entry){
            case EQUAL -> dk.kvalitetsit.itukt.common.model.Operator.EQUAL;
            case GREATER_THAN_OR_EQUAL_TO -> dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;
            case LESS_THAN_OR_EQUAL_TO -> dk.kvalitetsit.itukt.common.model.Operator.LESS_THAN_OR_EQUAL_TO;
            case GREATER_THAN -> dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN;
            case LESS_THAN -> dk.kvalitetsit.itukt.common.model.Operator.LESS_THAN;
        };
    }
}