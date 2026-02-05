package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.Operator;

import java.util.List;

public sealed interface Condition permits Condition.MultiValueCondition, Condition.SingleValueCondition {
    Identifier identifier();

    record SingleValueCondition(Identifier identifier, Operator operator, String value) implements Condition {
    }

    record MultiValueCondition(Identifier identifier, List<String> values) implements Condition {
    }
}
