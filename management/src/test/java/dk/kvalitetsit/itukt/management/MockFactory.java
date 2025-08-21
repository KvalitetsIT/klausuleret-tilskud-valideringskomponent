package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.Condition;
import org.openapitools.model.Operator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockFactory {

    public static final String dsl = "Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";
    public static final org.openapitools.model.Clause clauseDto;
    private static final ExpressionEntity expressionEntity = new ExpressionEntity.BinaryExpressionEntity(
            new ExpressionEntity.ConditionEntity(1L, "ATC", dk.kvalitetsit.itukt.common.model.Operator.EQUAL, List.of("C10BA03")),
            Expression.BinaryExpression.BinaryOperator.OR,
            new ExpressionEntity.BinaryExpressionEntity(
                    2L,
                    new ExpressionEntity.ConditionEntity(3L, "ATC", dk.kvalitetsit.itukt.common.model.Operator.IN, List.of("C10BA02", "C10BA05")),
                    Expression.BinaryExpression.BinaryOperator.AND,
                    new ExpressionEntity.ConditionEntity(3L, "ALDER", dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO, List.of("13"))
            ));
    public static final dk.kvalitetsit.itukt.common.model.Expression expressionModel = new dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression(
            new dk.kvalitetsit.itukt.common.model.Expression.Condition(((ExpressionEntity.ConditionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).left()).field(),
                    ((ExpressionEntity.ConditionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).left()).operator(),
                    ((ExpressionEntity.ConditionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).left()).values()
            ),
            dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression.BinaryOperator.OR,
            new dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression(
                    new dk.kvalitetsit.itukt.common.model.Expression.Condition(
                            ((ExpressionEntity.ConditionEntity) (((ExpressionEntity.BinaryExpressionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).right())).left()).field(),
                            ((ExpressionEntity.ConditionEntity) (((ExpressionEntity.BinaryExpressionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).right())).left()).operator(),
                            ((ExpressionEntity.ConditionEntity) (((ExpressionEntity.BinaryExpressionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).right())).left()).values()),
                    dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression.BinaryOperator.AND,
                    new dk.kvalitetsit.itukt.common.model.Expression.Condition(
                            ((ExpressionEntity.ConditionEntity) (((ExpressionEntity.BinaryExpressionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).right())).right()).field(),
                            ((ExpressionEntity.ConditionEntity) (((ExpressionEntity.BinaryExpressionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).right())).right()).operator(),
                            ((ExpressionEntity.ConditionEntity) (((ExpressionEntity.BinaryExpressionEntity) ((ExpressionEntity.BinaryExpressionEntity) expressionEntity).right())).right()).values()
                    )
            )
    );

    public static final org.openapitools.model.Expression expressionDto = new BinaryExpression()
            .type("BinaryExpression")
            .operator(BinaryOperator.OR)
            .left(new Condition()
                    .type("Condition")
                    .field(((Expression.Condition) ((Expression.BinaryExpression) expressionModel).left()).field())
                    .operator(Operator.EQUAL)
                    .values(((Expression.Condition) ((Expression.BinaryExpression) expressionModel).left()).values())
            )
            .right(new BinaryExpression()
                    .type("BinaryExpression")
                    .left(new Condition().type("Condition")
                            .field(((Expression.Condition) ((Expression.BinaryExpression) ((Expression.BinaryExpression) expressionModel).right()).left()).field())
                            .operator(Operator.I)
                            .values(((Expression.Condition) ((Expression.BinaryExpression) ((Expression.BinaryExpression) expressionModel).right()).left()).values())
                    )
                    .operator(BinaryOperator.AND)
                    .right(new Condition().type("Condition")
                            .field(((Expression.Condition) ((Expression.BinaryExpression) ((Expression.BinaryExpression) expressionModel).right()).right()).field())
                            .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                            .values(((Expression.Condition) ((Expression.BinaryExpression) ((Expression.BinaryExpression) expressionModel).right()).right()).values()))
            );

    public static ClauseEntity clauseEntity = new ClauseEntity(UUID.randomUUID(), "CHOL", expressionEntity);
    public static final dk.kvalitetsit.itukt.common.model.Clause clauseModel = new dk.kvalitetsit.itukt.common.model.Clause(clauseEntity.name(), Optional.of(clauseEntity.uuid()), expressionModel);

    static {
        clauseDto = new org.openapitools.model.Clause("CHOL", expressionDto);
        clauseDto.setUuid(clauseModel.uuid());
    }


}
