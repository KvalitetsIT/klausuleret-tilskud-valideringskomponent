package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.BinaryExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.NumberConditionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.StringConditionEntity;
import org.openapitools.model.*;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.Error;
import org.openapitools.model.Operator;

import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

public class MockFactory {

    private static final StringConditionEntity EXPRESSION_2_ENTITY = new StringConditionEntity(2L, Field.INDICATION, "C10BA03");
    private static final StringConditionEntity EXPRESSION_3_ENTITY = new StringConditionEntity(3L, Field.INDICATION, "C10BA02");
    private static final StringConditionEntity EXPRESSION_4_ENTITY = new StringConditionEntity(4L, Field.INDICATION, "C10BA05");
    private static final BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(EXPRESSION_3_ENTITY, dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.OR, EXPRESSION_4_ENTITY);
    private static final NumberConditionEntity EXPRESSION_6_ENTITY = new NumberConditionEntity(5L, Field.AGE, GREATER_THAN_OR_EQUAL_TO, 13);
    private static final BinaryExpressionEntity EXPRESSION_7_ENTITY = new BinaryExpressionEntity(
            5L,
            EXPRESSION_5_ENTITY,
            dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.AND,
            EXPRESSION_6_ENTITY
    );
    public static final ExpressionEntity EXPRESSION_1_ENTITY = new BinaryExpressionEntity(
            1L,
            EXPRESSION_2_ENTITY,
            dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.OR,
            EXPRESSION_7_ENTITY
    );
    public static ClauseEntity CLAUSE_1_ENTITY = new ClauseEntity(1L, UUID.randomUUID(), "CHOL", 10800, "message", EXPRESSION_1_ENTITY);
    private static final AgeConditionExpression EXPRESSION_6_MODEL = new AgeConditionExpression(
            EXPRESSION_6_ENTITY.operator(),
            EXPRESSION_6_ENTITY.value()
    );
    private static final AgeCondition EXPRESSION_6_DTO = new AgeCondition().type("NumberCondition")
            .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
            .value(EXPRESSION_6_MODEL.value());
    private static final IndicationConditionExpression EXPRESSION_3_MODEL = new IndicationConditionExpression(
            EXPRESSION_3_ENTITY.value());
    private static final IndicationCondition EXPRESSION_3_DTO = new IndicationCondition().type("StringCondition")
            .value((EXPRESSION_3_MODEL.requiredValue()));
    private static final IndicationConditionExpression EXPRESSION_4_MODEL = new IndicationConditionExpression(
            EXPRESSION_4_ENTITY.value());
    private static final dk.kvalitetsit.itukt.common.model.BinaryExpression EXPRESSION_5_MODEL = new dk.kvalitetsit.itukt.common.model.BinaryExpression(
            EXPRESSION_3_MODEL,
            EXPRESSION_5_ENTITY.operator(),
            EXPRESSION_4_MODEL);
    private static final IndicationCondition EXPRESSION_4_DTO = new IndicationCondition().type("StringCondition")
            .value((EXPRESSION_4_MODEL.requiredValue()));
    private static final BinaryExpression EXPRESSION_5_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_3_DTO)
            .operator(BinaryOperator.fromValue(EXPRESSION_5_MODEL.operator().name()))
            .right(EXPRESSION_4_DTO);
    private static final BinaryExpression EXPRESSION_7_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_5_DTO)
            .operator(BinaryOperator.AND)
            .right(EXPRESSION_6_DTO);
    private static final IndicationConditionExpression EXPRESSION_2_MODEL = new IndicationConditionExpression(
            EXPRESSION_2_ENTITY.value()
    );
    public static final dk.kvalitetsit.itukt.common.model.BinaryExpression EXPRESSION_1_MODEL = new dk.kvalitetsit.itukt.common.model.BinaryExpression(
            EXPRESSION_2_MODEL,
            dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.OR,
            EXPRESSION_7_MODEL()
    );
    public static final Clause CLAUSE_1_MODEL = new Clause(1L, CLAUSE_1_ENTITY.name(), CLAUSE_1_ENTITY.uuid(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL);

    private static final IndicationCondition EXPRESSION_2_DTO = new IndicationCondition()
            .type("StringCondition")
            .value((EXPRESSION_2_MODEL.requiredValue()));
    public static final org.openapitools.model.Expression EXPRESSION_1_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .operator(BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(EXPRESSION_7_DTO);
    public static final ClauseOutput CLAUSE_1_OUTPUT = new ClauseOutput(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO, new Error(CLAUSE_1_MODEL.error().message()), CLAUSE_1_MODEL.uuid());
    public static final ClauseInput CLAUSE_1_INPUT = new ClauseInput(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO, new Error(CLAUSE_1_MODEL.error().message()));

    public static String EXPRESSION_1_DSL = "(INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)";


    // Note: This clause(CLAUSE_1_DSL) matches: clause_1_*

    public static final DslInput CLAUSE_1_DSL_INPUT = new DslInput(new Error("message"), "Klausul CHOL: " + EXPRESSION_1_DSL);
    public static final DslOutput CLAUSE_1_DSL_OUTPUT = new DslOutput(new Error("message"), CLAUSE_1_DSL_INPUT.getDsl(), CLAUSE_1_MODEL.uuid());

    private static dk.kvalitetsit.itukt.common.model.BinaryExpression EXPRESSION_7_MODEL() {
        return new dk.kvalitetsit.itukt.common.model.BinaryExpression(
                EXPRESSION_5_MODEL,
                dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.AND,
                EXPRESSION_6_MODEL
        );
    }
}
