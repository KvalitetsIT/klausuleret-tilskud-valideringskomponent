package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.Expression;

import java.util.List;

public class SimpleConditionExpressionDslMapper<T extends Expression> implements ExpressionDslMapper<T> {
    private final Identifier identifier;
    private final StringToDslValueMapper stringToDslValueMapper;
    private final Mapper<T, String> conditionValueMapper;

    public SimpleConditionExpressionDslMapper(Identifier identifier, StringToDslValueMapper stringToDslValueMapper, Mapper<T, String> conditionValueMapper) {
        this.identifier = identifier;
        this.stringToDslValueMapper = stringToDslValueMapper;
        this.conditionValueMapper = conditionValueMapper;
    }

    @Override
    public String merge(List<T> expressions) {
        return ExpressionDtoDslMapper.mergeConditions(identifier, expressions, this::getValue);
    }

    @Override
    public Dsl map(T entry) {
        return new Dsl(identifier + " = " + getValue(entry), Dsl.Type.CONDITION);
    }

    private String getValue(T entry) {
        return stringToDslValueMapper.map(conditionValueMapper.map(entry));
    }
}
