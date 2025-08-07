package dk.kvalitetsit.itukt;

import org.openapitools.model.*;

import java.util.List;

public class MockFactory {

    public static final String dsl = """
            Klausul CHOL:
            (ATC = C10BA03)
            eller
            (ATC i C10BA02, C10BA05) og (ALDER >= 13)
            """;


    public static final Expression clause = new BinaryExpression()
            .type("BinaryExpression")
            .operator(BinaryOperator.OR)
            .left(new Condition().type("Condition").field("ATC").operator(Operator.EQUAL).values(List.of("C10BA03")))
            .right(new BinaryExpression().type("BinaryExpression").operator(BinaryOperator.AND)
                    .left(new Condition().type("Condition").field("ATC").operator(Operator.I).values(List.of("C10BA02", "C10BA05")))
                    .right(new Condition().type("Condition").field("ALDER").operator(Operator.GREATER_THAN_OR_EQUAL_TO).values(List.of("13"))
                    )
            );
}
