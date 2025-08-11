package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Operator;

class OperatorModelDtoMapper implements Mapper< dk.kvalitetsit.itukt.common.model.Operator, Operator >  {
    @Override
    public Operator map(dk.kvalitetsit.itukt.common.model.Operator entry) {
        return switch (entry){
            case EQUAL -> Operator.EQUAL;
            case IN -> Operator.I;
            case GREATER_THAN_OR_EQUAL_TO -> Operator.GREATER_THAN_OR_EQUAL_TO;
            case LESS_THAN_OR_EQUAL_TO -> Operator.LESS_THAN_OR_EQUAL_TO;
            case GREATER_THAN -> Operator.GREATER_THAN;
            case LESS_THAN -> Operator.LESS_THAN;
        };
    }
}