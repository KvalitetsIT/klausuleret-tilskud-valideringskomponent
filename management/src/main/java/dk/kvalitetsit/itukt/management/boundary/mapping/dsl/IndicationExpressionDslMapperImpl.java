package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import org.openapitools.model.IndicationCondition;

import java.util.List;

public class IndicationExpressionDslMapperImpl implements ExpressionDslMapper<IndicationCondition> {

    @Override
    public String merge(List<IndicationCondition> expressions) {
        return ExpressionDtoDslMapper.mergeConditions(Identifier.INDICATION, expressions, IndicationCondition::getValue);
    }

    @Override
    public Dsl map(IndicationCondition entry) {
        return new Dsl(Identifier.INDICATION + " = " + entry.getValue(), Dsl.Type.CONDITION);
    }
}
