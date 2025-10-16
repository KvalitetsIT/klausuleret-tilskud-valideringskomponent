package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.common.model.Error;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.openapitools.client.model.ClauseInput;
import org.openapitools.client.model.ClauseOutput;
import org.openapitools.client.model.DslInput;

import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

public class MockFactory {

    // Note: This clause(clause_1_dsl) matches: clause_1_*
    public static final DslInput CLAUSE_1_DSL_INPUT = new DslInput()
            .dsl("Klausul CHOL: (INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)")
            .error(new org.openapitools.client.model.Error().message("message"));

    public static final ExpressionEntity.Persisted.BinaryExpression EXPRESSION_1_ENTITY =
            new ExpressionEntity.Persisted.BinaryExpression(
                    1L,
                    new ExpressionEntity.Persisted.StringCondition(2L, Field.INDICATION, "C10BA03"),
                    BinaryOperator.OR,
                    new ExpressionEntity.Persisted.BinaryExpression(
                            5L,
                            new ExpressionEntity.Persisted.BinaryExpression(
                                    5L,
                                    new ExpressionEntity.Persisted.StringCondition(3L, Field.INDICATION, "C10BA02"),
                                    BinaryOperator.OR,
                                    new ExpressionEntity.Persisted.StringCondition(4L, Field.INDICATION, "C10BA05")
                            ),
                            BinaryOperator.AND,
                            new ExpressionEntity.Persisted.NumberCondition(6L, Field.AGE, GREATER_THAN_OR_EQUAL_TO, 13)
                    )
            );

    public static final ExpressionEntity.NotPersisted EXPRESSION_1_ENTITY_NP =
            new ExpressionEntity.NotPersisted.BinaryExpression(
                    new ExpressionEntity.NotPersisted.StringConditionEntity(Field.INDICATION, "C10BA03"),
                    BinaryOperator.OR,
                    new ExpressionEntity.NotPersisted.BinaryExpression(
                            new ExpressionEntity.NotPersisted.BinaryExpression(
                                    new ExpressionEntity.NotPersisted.StringConditionEntity(Field.INDICATION, "C10BA02"),
                                    BinaryOperator.OR,
                                    new ExpressionEntity.NotPersisted.StringConditionEntity(Field.INDICATION, "C10BA05")
                            ),
                            BinaryOperator.AND,
                            new ExpressionEntity.NotPersisted.NumberCondition(Field.AGE, GREATER_THAN_OR_EQUAL_TO, 13)
                    )
            );


    // Note: This clause(clause_1_entity) matches: clause_1_dsl
    public static final ClauseEntity.Persisted CLAUSE_1_ENTITY = new ClauseEntity.Persisted(
            1L,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "CHOL",
            0,
            "message",
            EXPRESSION_1_ENTITY
    );

    public static final Clause.Persisted CLAUSE_1_MODEL = new Clause.Persisted(
            1L,
            CLAUSE_1_ENTITY.uuid(),
            CLAUSE_1_ENTITY.name(),
            new Error("message", 10800),
            new Expression.Persisted.Binary(
                    EXPRESSION_1_ENTITY.id(),
                    new Expression.Persisted.Condition(
                            2L,
                            new Condition.Indication("C10BA03")
                    ),
                    BinaryOperator.OR,
                    new Expression.Persisted.Binary(
                            8L,
                            new Expression.Persisted.Binary(
                                    5L,
                                    new Expression.Persisted.Condition(
                                            3L,
                                            new Condition.Indication("C10BA02")
                                    ),
                                    BinaryOperator.OR,
                                    new Expression.Persisted.Condition(
                                            4L,
                                            new Condition.Indication("C10BA05")
                                    )
                            ),
                            BinaryOperator.AND,
                            new Expression.Persisted.Condition(
                                    6L,
                                    new Condition.Age(GREATER_THAN_OR_EQUAL_TO, 13)
                            )
                    )
            )
    );

    public static final ClauseOutput CLAUSE_1_OUTPUT = new ClauseOutput()
            .name("CHOL")
            .expression(
                    new org.openapitools.client.model.BinaryExpression()
                            .type("BinaryExpression")
                            .operator(org.openapitools.client.model.BinaryOperator.OR)
                            .left(new org.openapitools.client.model.IndicationCondition()
                                    .type("IndicationCondition")
                                    .value("C10BA03")
                            )
                            .right(
                                    new org.openapitools.client.model.BinaryExpression()
                                            .type("BinaryExpression")
                                            .operator(org.openapitools.client.model.BinaryOperator.AND)
                                            .left(
                                                    new org.openapitools.client.model.BinaryExpression()
                                                            .type("BinaryExpression")
                                                            .operator(org.openapitools.client.model.BinaryOperator.OR)
                                                            .left(new org.openapitools.client.model.IndicationCondition()
                                                                    .type("IndicationCondition")
                                                                    .value("C10BA02"))
                                                            .right(new org.openapitools.client.model.IndicationCondition()
                                                                    .type("IndicationCondition")
                                                                    .value("C10BA05"))
                                            )
                                            .right(new org.openapitools.client.model.AgeCondition()
                                                    .type("AgeCondition")
                                                    .operator(org.openapitools.client.model.Operator.GREATER_THAN_OR_EQUAL_TO)
                                                    .value(13)
                                            )
                            )
            )
            .uuid(CLAUSE_1_MODEL.uuid())
            .error(new org.openapitools.client.model.Error().message("message"));

    public static final ClauseInput CLAUSE_1_INPUT = new ClauseInput()
            .name("CHOL")
            .expression(
                    new org.openapitools.client.model.BinaryExpression()
                            .type("BinaryExpression")
                            .operator(org.openapitools.client.model.BinaryOperator.OR)
                            .left(new org.openapitools.client.model.IndicationCondition()
                                    .type("IndicationCondition")
                                    .value("C10BA03")
                            )
                            .right(
                                    new org.openapitools.client.model.BinaryExpression()
                                            .type("BinaryExpression")
                                            .operator(org.openapitools.client.model.BinaryOperator.AND)
                                            .left(
                                                    new org.openapitools.client.model.BinaryExpression()
                                                            .type("BinaryExpression")
                                                            .operator(org.openapitools.client.model.BinaryOperator.OR)
                                                            .left(new org.openapitools.client.model.IndicationCondition()
                                                                    .type("IndicationCondition")
                                                                    .value("C10BA02"))
                                                            .right(new org.openapitools.client.model.IndicationCondition()
                                                                    .type("IndicationCondition")
                                                                    .value("C10BA05"))
                                            )
                                            .right(new org.openapitools.client.model.AgeCondition()
                                                    .type("AgeCondition")
                                                    .operator(org.openapitools.client.model.Operator.GREATER_THAN_OR_EQUAL_TO)
                                                    .value(13)
                                            )
                            )
            )
            .error(new org.openapitools.client.model.Error().message("message"));

    public static org.openapitools.client.model.ExistingDrugMedicationCondition createExistingDrugMedicationCondition(
            String atcCode,
            String formCode,
            String routeOfAdministrationCode
    ) {
        return new org.openapitools.client.model.ExistingDrugMedicationCondition()
                .type("ExistingDrugMedicationCondition")
                .atcCode(atcCode)
                .formCode(formCode)
                .routeOfAdministrationCode(routeOfAdministrationCode);
    }

    public static org.openapitools.client.model.BinaryExpression createBinaryAndExpression(
            org.openapitools.client.model.Expression left,
            org.openapitools.client.model.Expression right
    ) {
        return new org.openapitools.client.model.BinaryExpression()
                .type("BinaryExpression")
                .left(left)
                .operator(org.openapitools.client.model.BinaryOperator.AND)
                .right(right);
    }
}


