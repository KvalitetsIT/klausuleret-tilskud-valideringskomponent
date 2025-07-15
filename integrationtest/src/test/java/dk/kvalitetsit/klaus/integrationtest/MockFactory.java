package dk.kvalitetsit.klaus.integrationtest;

import org.openapitools.client.model.BinaryExpression;
import org.openapitools.client.model.Clause;
import org.openapitools.client.model.Condition;
import org.openapitools.client.model.Expression;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockFactory {

    public static final String dsl = "Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";


    public static final Expression expressionDto = new BinaryExpression()
            .type("BinaryExpression")
            .operator("eller")
            .left(new Condition().type("Condition").field("ATC").operator("=").values(List.of("C10BA03")))
            .right(new BinaryExpression().type("BinaryExpression").operator("og")
                    .left(new Condition().type("Condition").field("ATC").operator("i").values(List.of("C10BA02", "C10BA05")))
                    .right(new Condition().type("Condition").field("ALDER").operator(">=").values(List.of("13")))
            );

    public static final Clause clauseDto = new Clause().name("CHOL").expression(expressionDto);

    public static final dk.kvalitetsit.klaus.model.Expression expressionModel = new dk.kvalitetsit.klaus.model.Expression.BinaryExpression(
            new dk.kvalitetsit.klaus.model.Expression.Condition("ATC", "=", List.of("C10BA03")),
            "eller",
            new dk.kvalitetsit.klaus.model.Expression.BinaryExpression(
                    new dk.kvalitetsit.klaus.model.Expression.Condition("ATC", "i", List.of("C10BA02", "C10BA05")),
                    "og",
                    new dk.kvalitetsit.klaus.model.Expression.Condition("ALDER", ">=", List.of("13"))
            ));

    public static final dk.kvalitetsit.klaus.model.Clause clauseModel = new dk.kvalitetsit.klaus.model.Clause("CHOL", Optional.of(UUID.randomUUID()), Optional.of(1), expressionModel);
}

