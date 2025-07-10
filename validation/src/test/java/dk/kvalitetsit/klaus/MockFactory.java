package dk.kvalitetsit.klaus;

import org.openapitools.model.BinaryExpression;
import org.openapitools.model.Condition;
import org.openapitools.model.Expression;

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
            .operator("eller")
            .left(new Condition().type("Condition").field("ATC").operator("=").values(List.of("C10BA03")))
            .right(new BinaryExpression().type("BinaryExpression").operator("og")
                    .left(new Condition().type("Condition").field("ATC").operator("i").values(List.of("C10BA02", "C10BA05")))
                    .right(new Condition().type("Condition").field("ALDER").operator(">=").values(List.of("13"))
                    )
            );
}
