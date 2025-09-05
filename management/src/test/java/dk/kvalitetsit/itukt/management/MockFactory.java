package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.BinaryExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.ConditionEntity;
import org.openapitools.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.*;

public class MockFactory {

    public static final ConditionEntity EXPRESSION_2_ENTITY = new ConditionEntity(2L, "ATC", EQUAL, List.of("C10BA03"));
    public static final ConditionEntity EXPRESSION_3_ENTITY = new ConditionEntity(3L, "ATC", IN, List.of("C10BA02", "C10BA05"));
    public static final ConditionEntity EXPRESSION_4_ENTITY = new ConditionEntity(3L, "ALDER", GREATER_THAN_OR_EQUAL_TO, List.of("13"));
    public static final Expression.Condition EXPRESSION_4_MODEL = new Expression.Condition(
            EXPRESSION_4_ENTITY.field(),
            EXPRESSION_4_ENTITY.operator(),
            EXPRESSION_4_ENTITY.values()
    );

    public static final Condition EXPRESSION_4_DTO = new Condition().type("Condition")
            .field(EXPRESSION_4_MODEL.field())
            .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
            .values(EXPRESSION_4_MODEL.values());

    public static final Expression.Condition EXPRESSION_3_MODEL = new Expression.Condition(
            EXPRESSION_3_ENTITY.field(),
            EXPRESSION_3_ENTITY.operator(),
            EXPRESSION_3_ENTITY.values());

    public static final Condition EXPRESSION_3_DTO = new Condition().type("Condition")
            .field(EXPRESSION_3_MODEL.field())
            .operator(Operator.I)
            .values((EXPRESSION_3_MODEL.values()));

    public static final Expression.Condition EXPRESSION_2_MODEL = new Expression.Condition(
            EXPRESSION_2_ENTITY.field(),
            EXPRESSION_2_ENTITY.operator(),
            EXPRESSION_2_ENTITY.values()
    );
    public static final Expression.BinaryExpression EXPRESSION_1_MODEL = new Expression.BinaryExpression(
            EXPRESSION_2_MODEL,
            Expression.BinaryExpression.BinaryOperator.OR,
            EXPRESSION_5_MODEL()
    );
    public static final Condition EXPRESSION_2_DTO = new Condition()
            .type("Condition")
            .field(EXPRESSION_2_MODEL.field())
            .operator(Operator.EQUAL)
            .values((EXPRESSION_2_MODEL.values()));

    public static final BinaryExpression EXPRESSION_5_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_3_DTO)
            .operator(BinaryOperator.AND)
            .right(EXPRESSION_4_DTO);

    public static final org.openapitools.model.Expression EXPRESSION_1_DTO = new BinaryExpression()
            .type("BinaryExpression")
            .operator(BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(EXPRESSION_5_DTO);

    public static final BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(
            5L,
            EXPRESSION_3_ENTITY,
            Expression.BinaryExpression.BinaryOperator.AND,
            EXPRESSION_4_ENTITY
    );

    public static final ExpressionEntity EXPRESSION_1_ENTITY = new BinaryExpressionEntity(
            1L,
            EXPRESSION_2_ENTITY,
            Expression.BinaryExpression.BinaryOperator.OR,
            EXPRESSION_5_ENTITY
    );

    public static ClauseEntity CLAUSE_1_ENTITY = new ClauseEntity(null, UUID.randomUUID(), "CHOL", EXPRESSION_1_ENTITY);
    public static final Clause CLAUSE_1_MODEL = new Clause(CLAUSE_1_ENTITY.name(), Optional.of(CLAUSE_1_ENTITY.uuid()), EXPRESSION_1_MODEL);
    public static final ClauseOutput CLAUSE_1_OUTPUT = new ClauseOutput(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO, null, CLAUSE_1_MODEL.uuid().get());
    public static final ClauseInput CLAUSE_1_INPUT = new ClauseInput(CLAUSE_1_ENTITY.name(), EXPRESSION_1_DTO);
    public static String EXPRESSION_1_DSL = "(ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";

    // Note: This clause(CLAUSE_1_DSL) matches: clause_1_*

    public static final DslInput CLAUSE_1_DSL_INPUT = new DslInput("Klausul CHOL: " + EXPRESSION_1_DSL);
    public static final DslOutput CLAUSE_1_DSL_OUTPUT = new DslOutput(CLAUSE_1_MODEL.uuid().get(), CLAUSE_1_DSL_INPUT.getDsl());

    private static Expression.BinaryExpression EXPRESSION_5_MODEL() {
        return new Expression.BinaryExpression(
                EXPRESSION_3_MODEL,
                Expression.BinaryExpression.BinaryOperator.AND,
                EXPRESSION_4_MODEL
        );
    }
}
