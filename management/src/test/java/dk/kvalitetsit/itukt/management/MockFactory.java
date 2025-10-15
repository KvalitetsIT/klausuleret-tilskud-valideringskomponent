package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.openapitools.model.*;
import org.openapitools.model.Error;

import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

public class MockFactory {

    private static final ExpressionEntity.Persisted.StringCondition EXPRESSION_2_ENTITY = new ExpressionEntity.Persisted.StringCondition(2L, Field.INDICATION, "C10BA03");
    private static final ExpressionEntity.Persisted.StringCondition EXPRESSION_3_ENTITY = new ExpressionEntity.Persisted.StringCondition(3L, Field.INDICATION, "C10BA02");
    private static final ExpressionEntity.Persisted.StringCondition EXPRESSION_4_ENTITY = new ExpressionEntity.Persisted.StringCondition(4L, Field.INDICATION, "C10BA05");
    private static final ExpressionEntity.Persisted.BinaryExpression EXPRESSION_5_ENTITY = new ExpressionEntity.Persisted.BinaryExpression(
            5L,
            EXPRESSION_3_ENTITY,
            BinaryOperator.OR,
            EXPRESSION_4_ENTITY
    );
    private static final ExpressionEntity.Persisted.NumberCondition EXPRESSION_6_ENTITY = new ExpressionEntity.Persisted.NumberCondition(6L, Field.AGE, GREATER_THAN_OR_EQUAL_TO, 13);
    private static final ExpressionEntity.Persisted.BinaryExpression EXPRESSION_7_ENTITY = new ExpressionEntity.Persisted.BinaryExpression(
            7L,
            EXPRESSION_5_ENTITY,
            BinaryOperator.AND,
            EXPRESSION_6_ENTITY
    );
    public static final ExpressionEntity.Persisted.BinaryExpression EXPRESSION_1_ENTITY = new ExpressionEntity.Persisted.BinaryExpression(
            1L,
            EXPRESSION_2_ENTITY,
            BinaryOperator.OR,
            EXPRESSION_7_ENTITY
    );


    public static ClauseEntity.Persisted CLAUSE_1_ENTITY = new ClauseEntity.Persisted(1L, UUID.randomUUID(), "CHOL", 10800, "message", EXPRESSION_1_ENTITY);
    private static final Condition.Age EXPRESSION_6_MODEL = new Condition.Age(
            EXPRESSION_6_ENTITY.operator(),
            EXPRESSION_6_ENTITY.value()
    );
    private static final AgeCondition EXPRESSION_6_DTO = new AgeCondition().type("NumberCondition")
            .operator(org.openapitools.model.Operator.GREATER_THAN_OR_EQUAL_TO)
            .value(EXPRESSION_6_MODEL.value());
    private static final Condition.Indication EXPRESSION_3_MODEL = new Condition.Indication(
            EXPRESSION_3_ENTITY.value());
    private static final IndicationCondition EXPRESSION_3_DTO = new IndicationCondition().type("StringCondition")
            .value((EXPRESSION_3_MODEL.requiredValue()));
    private static final Condition.Indication EXPRESSION_4_MODEL = new Condition.Indication(
            EXPRESSION_4_ENTITY.value());
    private static final Expression.Persisted.Binary EXPRESSION_5_MODEL = new Expression.Persisted.Binary(EXPRESSION_5_ENTITY.id(),
            new Expression.Persisted.Condition(EXPRESSION_3_ENTITY.id(), EXPRESSION_3_MODEL),
            EXPRESSION_5_ENTITY.operator(),
            new Expression.Persisted.Condition(EXPRESSION_4_ENTITY.id(), EXPRESSION_4_MODEL)
    );
    private static final IndicationCondition EXPRESSION_4_DTO = new IndicationCondition().type("StringCondition")
            .value((EXPRESSION_4_MODEL.requiredValue()));

    private static final BinaryExpression EXPRESSION_5_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_3_DTO)
            .operator(org.openapitools.model.BinaryOperator.fromValue(EXPRESSION_5_MODEL.operator().name()))
            .right(EXPRESSION_4_DTO);
    private static final BinaryExpression EXPRESSION_7_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_5_DTO)
            .operator(org.openapitools.model.BinaryOperator.AND)
            .right(EXPRESSION_6_DTO);

    private static final Condition.Indication EXPRESSION_2_MODEL = new Condition.Indication(
            EXPRESSION_2_ENTITY.value()
    );
    public static final Expression.Persisted.Binary EXPRESSION_1_MODEL = new Expression.Persisted.Binary(1L,
            new Expression.Persisted.Condition(EXPRESSION_2_ENTITY.id(), EXPRESSION_2_MODEL),
            BinaryOperator.OR,
            EXPRESSION_7_MODEL()
    );

    public static final Clause.Persisted CLAUSE_1_MODEL = new Clause.Persisted(1L, CLAUSE_1_ENTITY.uuid(), CLAUSE_1_ENTITY.name(), new dk.kvalitetsit.itukt.common.model.Error("message", 10800), EXPRESSION_1_MODEL);

    private static final IndicationCondition EXPRESSION_2_DTO = new IndicationCondition()
            .type("StringCondition")
            .value((EXPRESSION_2_MODEL.requiredValue()));
    public static final org.openapitools.model.Expression EXPRESSION_1_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .operator(org.openapitools.model.BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(EXPRESSION_7_DTO);

    public static final ClauseOutput CLAUSE_1_OUTPUT = new ClauseOutput(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO, new Error(CLAUSE_1_MODEL.error().message()), CLAUSE_1_MODEL.uuid());
    public static final ClauseInput CLAUSE_1_INPUT = new ClauseInput(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO, new Error(CLAUSE_1_MODEL.error().message()));

    public static String EXPRESSION_1_DSL = "(INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)";


    // Note: This clause(CLAUSE_1_DSL) matches: clause_1_*

    public static final DslInput CLAUSE_1_DSL_INPUT = new DslInput(new Error("message"), "Klausul CHOL: " + EXPRESSION_1_DSL);
    public static final DslOutput CLAUSE_1_DSL_OUTPUT = new DslOutput(new Error("message"), CLAUSE_1_DSL_INPUT.getDsl(), CLAUSE_1_MODEL.uuid());

    private static Expression.Persisted.Binary EXPRESSION_7_MODEL() {
        return new Expression.Persisted.Binary(
                EXPRESSION_7_ENTITY.id(),
                EXPRESSION_5_MODEL,
                BinaryOperator.AND,
                new Expression.Persisted.Condition(EXPRESSION_6_ENTITY.id(), EXPRESSION_6_MODEL)
        );
    }
}
