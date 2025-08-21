package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.BinaryExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.ConditionEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.*;

public class MockFactory {

    // Note: This clause(clause_1_dsl) matches: clause_1_*
    public static final String CLAUSE_1_DSL = "Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";

    public static final ConditionEntity EXPRESSION_2_ENTITY = new ConditionEntity(2L, "ATC", EQUAL, List.of("C10BA03"));

    public static final ConditionEntity EXPRESSION_3_ENTITY = new ConditionEntity(3L, "ATC", IN, List.of("C10BA02", "C10BA05"));

    public static final ConditionEntity EXPRESSION_4_ENTITY = new ConditionEntity(3L, "ALDER", GREATER_THAN_OR_EQUAL_TO, List.of("13"));

    public static final Expression.Condition EXPRESSION_4_MODEL = new Expression.Condition(
            EXPRESSION_4_ENTITY.field(),
            EXPRESSION_4_ENTITY.operator(),
            EXPRESSION_4_ENTITY.values()
    );

    public static final org.openapitools.client.model.Condition EXPRESSION_4_DTO = new org.openapitools.client.model.Condition().type("Condition")
            .field(EXPRESSION_4_MODEL.field())
            .operator(org.openapitools.client.model.Operator.GREATER_THAN_OR_EQUAL_TO)
            .values(EXPRESSION_4_MODEL.values());

    public static final Expression.Condition EXPRESSION_3_MODEL = new Expression.Condition(
            EXPRESSION_3_ENTITY.field(),
            EXPRESSION_3_ENTITY.operator(),
            EXPRESSION_3_ENTITY.values());

    public static final org.openapitools.client.model.Condition EXPRESSION_3_DTO = new org.openapitools.client.model.Condition().type("Condition")
            .field(EXPRESSION_3_MODEL.field())
            .operator(org.openapitools.client.model.Operator.I)
            .values((EXPRESSION_3_MODEL.values()));

    public static final Expression.Condition EXPRESSION_2_MODEL = new Expression.Condition(
            EXPRESSION_2_ENTITY.field(),
            EXPRESSION_2_ENTITY.operator(),
            EXPRESSION_2_ENTITY.values()
    );

    public static final Expression.BinaryExpression EXPRESSION_1_MODEL = new Expression.BinaryExpression(
            EXPRESSION_2_MODEL,
            Expression.BinaryExpression.BinaryOperator.OR,
            new Expression.BinaryExpression(
                    EXPRESSION_3_MODEL,
                    Expression.BinaryExpression.BinaryOperator.AND,
                    EXPRESSION_4_MODEL
            )
    );

    public static final org.openapitools.client.model.Condition EXPRESSION_2_DTO = new org.openapitools.client.model.Condition()
            .type("Condition")
            .field(EXPRESSION_2_MODEL.field())
            .operator(org.openapitools.client.model.Operator.EQUAL)
            .values((EXPRESSION_2_MODEL.values()));

    public static final org.openapitools.client.model.Expression EXPRESSION_1_DTO = new org.openapitools.client.model.BinaryExpression()
            .type("BinaryExpression")
            .operator(org.openapitools.client.model.BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(new org.openapitools.client.model.BinaryExpression()
                    .type("BinaryExpression")
                    .left(EXPRESSION_3_DTO)
                    .operator(org.openapitools.client.model.BinaryOperator.AND)
                    .right(EXPRESSION_4_DTO)
            );

    @NotNull
    private static BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(
            5L,
            EXPRESSION_3_ENTITY,
            Expression.BinaryExpression.BinaryOperator.AND,
            EXPRESSION_4_ENTITY
    );

    private static final ExpressionEntity EXPRESSION_1_ENTITY = new BinaryExpressionEntity(
            1L,
            EXPRESSION_2_ENTITY,
            Expression.BinaryExpression.BinaryOperator.OR,
            EXPRESSION_5_ENTITY
    );

    // Note: This clause(clause_1_entity) matches: clause_1_dsl
    public static ClauseEntity CLAUSE_1_ENTITY = new ClauseEntity(1L, UUID.randomUUID(), "CHOL", EXPRESSION_1_ENTITY);

    public static final Clause CLAUSE_1_MODEL = new Clause(CLAUSE_1_ENTITY.name(), Optional.of(CLAUSE_1_ENTITY.uuid()), EXPRESSION_1_MODEL);

    public static final org.openapitools.client.model.Clause CLAUSE_1_DTO = new org.openapitools.client.model.Clause()
            .name("CHOL")
            .expression(EXPRESSION_1_DTO)
            .uuid(CLAUSE_1_MODEL.uuid().get());


}

