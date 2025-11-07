package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Expression;

import java.util.List;

public interface ExpressionDslMapper<T extends Expression> extends Mapper<T, Dsl> {
    String merge(List<T> expressions);
}
