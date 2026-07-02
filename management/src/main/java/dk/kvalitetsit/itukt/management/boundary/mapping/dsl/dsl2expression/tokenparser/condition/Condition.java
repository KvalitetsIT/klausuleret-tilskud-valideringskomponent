package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedValueException;
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
        default Simple asSimple() throws DslParserException {
            return switch (this) {
                case Simple simple -> simple;
                case Structured structured ->
                        throw new UnexpectedValueException(structured.values().toString());
            };
        }

        default Structured asStructured() throws DslParserException {
            return switch (this) {
                case Simple simple ->
                        throw new UnexpectedValueException(simple.value());
                case Structured structured -> structured;
            };
        }

        record Simple(String value) implements Value {
        }

        record Structured(Map<String, String> values) implements Value {
        }
    }
}
