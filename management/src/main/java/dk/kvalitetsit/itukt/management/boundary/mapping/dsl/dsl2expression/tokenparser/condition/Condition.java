package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import org.openapitools.model.Operator;

import java.util.List;
import java.util.Map;

public sealed interface Condition permits Condition.MultiValueCondition, Condition.SingleValueCondition {
    Identifier identifier();

    record SingleValueCondition(Identifier identifier, Operator operator, Value value) implements Condition {
    }

    record MultiValueCondition(Identifier identifier, List<Value> values) implements Condition {
    }

    sealed interface Value permits Value.Simple, Value.Structured {
        default Simple asSimple() {
            return switch (this) {
                case Simple simple -> simple;
                case Structured structured ->
                        throw new DslParserException("Expected simple value but got structured value: " + structured.values());
            };
        }

        default Structured asStructured() {
            return switch (this) {
                case Simple simple ->
                        throw new DslParserException("Expected structured value but got simple value: " + simple.value());
                case Structured structured -> structured;
            };
        }

        record Simple(String value) implements Value {
        }

        record Structured(Map<String, String> values) implements Value {
        }
    }
}
