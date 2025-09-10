package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.BinaryExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.ConditionEntity;

import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.EQUAL;
import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

public class MockFactory {

    // Note: This clause(clause_1_dsl) matches: clause_1_*
    public static final String CLAUSE_1_DSL = "Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";

    private static final ConditionEntity EXPRESSION_2_ENTITY = new ConditionEntity(2L, "ATC", EQUAL, "C10BA03");

    private static final ConditionEntity EXPRESSION_3_ENTITY = new ConditionEntity(3L, "ATC", EQUAL, "C10BA02");

    private static final ConditionEntity EXPRESSION_4_ENTITY = new ConditionEntity(4L, "ATC", EQUAL, "C10BA05");

    private static final BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(EXPRESSION_3_ENTITY, Expression.BinaryExpression.BinaryOperator.OR, EXPRESSION_4_ENTITY);

    private static final ConditionEntity EXPRESSION_6_ENTITY = new ConditionEntity(6L, "ALDER", GREATER_THAN_OR_EQUAL_TO, "13");

    private static final Expression.Condition EXPRESSION_6_MODEL = new Expression.Condition(
            EXPRESSION_6_ENTITY.field(),
            EXPRESSION_6_ENTITY.operator(),
            EXPRESSION_6_ENTITY.value()
    );

    private static final org.openapitools.client.model.Condition EXPRESSION_6_DTO = new org.openapitools.client.model.Condition().type("Condition")
            .field(EXPRESSION_6_MODEL.field())
            .operator(org.openapitools.client.model.Operator.GREATER_THAN_OR_EQUAL_TO)
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

    private static final org.openapitools.client.model.Condition EXPRESSION_3_DTO = new org.openapitools.client.model.Condition().type("Condition")
            .field(EXPRESSION_3_MODEL.field())
            .operator(org.openapitools.client.model.Operator.fromValue(EXPRESSION_3_MODEL.operator().getValue()))
            .value((EXPRESSION_3_MODEL.value()));

    private static final org.openapitools.client.model.Condition EXPRESSION_4_DTO = new org.openapitools.client.model.Condition().type("Condition")
            .field(EXPRESSION_4_MODEL.field())
            .operator(org.openapitools.client.model.Operator.fromValue(EXPRESSION_4_MODEL.operator().getValue()))
            .value(EXPRESSION_4_MODEL.value());

    private static final org.openapitools.client.model.BinaryExpression EXPRESSION_5_DTO = new org.openapitools.client.model.BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_3_DTO)
            .operator(org.openapitools.client.model.BinaryOperator.fromValue(EXPRESSION_5_MODEL.operator().name()))
            .right(EXPRESSION_4_DTO);

    private static final Expression.Condition EXPRESSION_2_MODEL = new Expression.Condition(
            EXPRESSION_2_ENTITY.field(),
            EXPRESSION_2_ENTITY.operator(),
            EXPRESSION_2_ENTITY.value()
    );

    private static final Expression.BinaryExpression EXPRESSION_1_MODEL = new Expression.BinaryExpression(
            EXPRESSION_2_MODEL,
            Expression.BinaryExpression.BinaryOperator.OR,
            new Expression.BinaryExpression(
                    EXPRESSION_5_MODEL,
                    Expression.BinaryExpression.BinaryOperator.AND,
                    EXPRESSION_6_MODEL
            )
    );

    private static final org.openapitools.client.model.Condition EXPRESSION_2_DTO = new org.openapitools.client.model.Condition()
            .type("Condition")
            .field(EXPRESSION_2_MODEL.field())
            .operator(org.openapitools.client.model.Operator.EQUAL)
            .value((EXPRESSION_2_MODEL.value()));

    private static final org.openapitools.client.model.Expression EXPRESSION_1_DTO = new org.openapitools.client.model.BinaryExpression()
            .type("BinaryExpression")
            .operator(org.openapitools.client.model.BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(new org.openapitools.client.model.BinaryExpression()
                    .type("BinaryExpression")
                    .left(EXPRESSION_5_DTO)
                    .operator(org.openapitools.client.model.BinaryOperator.AND)
                    .right(EXPRESSION_6_DTO)
            );

    private static final BinaryExpressionEntity EXPRESSION_7_ENTITY = new BinaryExpressionEntity(
            5L,
            EXPRESSION_5_ENTITY,
            Expression.BinaryExpression.BinaryOperator.AND,
            EXPRESSION_6_ENTITY
    );

    private static final ExpressionEntity EXPRESSION_1_ENTITY = new BinaryExpressionEntity(
            1L,
            EXPRESSION_2_ENTITY,
            Expression.BinaryExpression.BinaryOperator.OR,
            EXPRESSION_7_ENTITY
    );

    // Note: This clause(clause_1_entity) matches: clause_1_dsl
    public static ClauseEntity CLAUSE_1_ENTITY = new ClauseEntity(null, UUID.randomUUID(), "CHOL", EXPRESSION_1_ENTITY);

    public static final Clause CLAUSE_1_MODEL = new Clause(CLAUSE_1_ENTITY.name(), Optional.of(CLAUSE_1_ENTITY.uuid()), EXPRESSION_1_MODEL);

    public static final org.openapitools.client.model.Clause CLAUSE_1_DTO = new org.openapitools.client.model.Clause()
            .name("CHOL")
            .expression(EXPRESSION_1_DTO)
            .uuid(CLAUSE_1_MODEL.uuid().get());


}

