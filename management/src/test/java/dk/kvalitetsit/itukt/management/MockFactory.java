package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.BinaryExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.ConditionEntity;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.Condition;
import org.openapitools.model.Operator;

import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.EQUAL;
import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

public class MockFactory {

    private static final ConditionEntity EXPRESSION_2_ENTITY = new ConditionEntity(2L, "ATC", EQUAL, "C10BA03");
    private static final ConditionEntity EXPRESSION_3_ENTITY = new ConditionEntity(3L, "ATC", EQUAL, "C10BA02");
    private static final ConditionEntity EXPRESSION_4_ENTITY = new ConditionEntity(4L, "ATC", EQUAL, "C10BA05");
    private static final BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(EXPRESSION_3_ENTITY, Expression.BinaryExpression.BinaryOperator.OR, EXPRESSION_4_ENTITY);
    private static final ConditionEntity EXPRESSION_6_ENTITY = new ConditionEntity(5L, "ALDER", GREATER_THAN_OR_EQUAL_TO, "13");
    
    private static final Expression.Condition EXPRESSION_6_MODEL = new Expression.Condition(
            EXPRESSION_6_ENTITY.field(),
            EXPRESSION_6_ENTITY.operator(),
            EXPRESSION_6_ENTITY.value()
    );

    private static final Condition EXPRESSION_6_DTO = new Condition().type("Condition")
            .field(EXPRESSION_6_MODEL.field())
            .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
            .value(EXPRESSION_6_MODEL.value());

    private static final Expression.Condition EXPRESSION_3_MODEL = new Expression.Condition(
            EXPRESSION_3_ENTITY.field(),
            EXPRESSION_3_ENTITY.operator(),
            EXPRESSION_3_ENTITY.value());

    private static final Expression.Condition EXPRESSION_4_MODEL = new Expression.Condition(
            EXPRESSION_4_ENTITY.field(),
            EXPRESSION_4_ENTITY.operator(),
            EXPRESSION_4_ENTITY.value());

    private static final Expression.BinaryExpression EXPRESSION_5_MODEL = new Expression.BinaryExpression(
            EXPRESSION_3_MODEL,
            EXPRESSION_5_ENTITY.operator(),
            EXPRESSION_4_MODEL);

    private static final Condition EXPRESSION_3_DTO = new Condition().type("Condition")
            .field(EXPRESSION_3_MODEL.field())
            .operator(Operator.fromValue(EXPRESSION_3_MODEL.operator().getValue()))
            .value((EXPRESSION_3_MODEL.value()));

    private static final Condition EXPRESSION_4_DTO = new Condition().type("Condition")
            .field(EXPRESSION_4_MODEL.field())
            .operator(Operator.fromValue(EXPRESSION_4_MODEL.operator().getValue()))
            .value((EXPRESSION_4_MODEL.value()));

    private static final BinaryExpression EXPRESSION_5_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_3_DTO)
            .operator(BinaryOperator.fromValue(EXPRESSION_5_MODEL.operator().name()))
            .right(EXPRESSION_4_DTO);

    private static final Expression.Condition EXPRESSION_2_MODEL = new Expression.Condition(
            EXPRESSION_2_ENTITY.field(),
            EXPRESSION_2_ENTITY.operator(),
            EXPRESSION_2_ENTITY.value()
    );
    public static final Expression.BinaryExpression EXPRESSION_1_MODEL = new Expression.BinaryExpression(
            EXPRESSION_2_MODEL,
            Expression.BinaryExpression.BinaryOperator.OR,
            EXPRESSION_7_MODEL()
    );
    private static final Condition EXPRESSION_2_DTO = new Condition()
            .type("Condition")
            .field(EXPRESSION_2_MODEL.field())
            .operator(Operator.EQUAL)
            .value((EXPRESSION_2_MODEL.value()));

    private static final BinaryExpression EXPRESSION_7_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_5_DTO)
            .operator(BinaryOperator.AND)
            .right(EXPRESSION_6_DTO);

    public static final org.openapitools.model.Expression EXPRESSION_1_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .operator(BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(EXPRESSION_7_DTO);

    private static final BinaryExpressionEntity EXPRESSION_7_ENTITY = new BinaryExpressionEntity(
            5L,
            EXPRESSION_5_ENTITY,
            Expression.BinaryExpression.BinaryOperator.AND,
            EXPRESSION_6_ENTITY
    );

    public static final ExpressionEntity EXPRESSION_1_ENTITY = new BinaryExpressionEntity(
            1L,
            EXPRESSION_2_ENTITY,
            Expression.BinaryExpression.BinaryOperator.OR,
            EXPRESSION_7_ENTITY
    );

    public static ClauseEntity CLAUSE_1_ENTITY = new ClauseEntity(null, UUID.randomUUID(), "CHOL", EXPRESSION_1_ENTITY);
    public static final Clause CLAUSE_1_MODEL = new Clause(CLAUSE_1_ENTITY.name(), Optional.of(CLAUSE_1_ENTITY.uuid()), EXPRESSION_1_MODEL);
    public static final org.openapitools.model.Clause CLAUSE_1_DTO = new org.openapitools.model.Clause(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO).uuid(CLAUSE_1_MODEL.uuid().get());
    public static String EXPRESSION_1_DSL = "(ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";

    // Note: This clause(CLAUSE_1_DSL) matches: clause_1_*
    public static final String CLAUSE_1_DSL = "Klausul CHOL: " + EXPRESSION_1_DSL;

    private static Expression.BinaryExpression EXPRESSION_7_MODEL() {
        return new Expression.BinaryExpression(
                EXPRESSION_5_MODEL,
                Expression.BinaryExpression.BinaryOperator.AND,
                EXPRESSION_6_MODEL
        );
    }
}
