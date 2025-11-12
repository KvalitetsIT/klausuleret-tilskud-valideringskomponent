package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Expression;

import java.util.List;

public interface ExpressionDslMapper<T extends Expression> extends Mapper<T, Dsl> {
    /**
     * Merges expressions into a "merged" DSL format
     * </br>
     * </br>
     * Ex.
     * </br>
     * The conditions below:
     * <pre>
     * {@code
     *     var conditions = List.of(
     *         new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10),
     *         new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20)
     *     );
     * }
     * </pre>
     *  will be merged into this:
     * <pre>
     * {@code
     *     ALDER i [10, 20];
     * }
     * </pre>
     * @param expressions mergeable expressions
     * @return the merged expressions in DSL format. Ex. ATC i [something, somethingElse]
     */
    String merge(List<T> expressions);
}
