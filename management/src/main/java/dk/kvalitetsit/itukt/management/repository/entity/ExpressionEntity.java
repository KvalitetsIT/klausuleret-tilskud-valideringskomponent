package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;

import java.util.List;

public sealed interface ExpressionEntity
        permits ExpressionEntity.ConditionEntity,
        ExpressionEntity.BinaryExpressionEntity,
        ExpressionEntity.ParenthesizedExpressionEntity {

    ExpressionType type();

    Long id();

    ExpressionEntity withId(Long newId);

    record ConditionEntity(Long id, String field, Operator operator, List<String> values)
            implements ExpressionEntity {

        public ConditionEntity(String field, Operator operator, List<String> values) {
            this(null, field, operator, values);
        }

        @Override
        public ExpressionType type() {
            return ExpressionType.CONDITION;
        }

        @Override
        public ConditionEntity withId(Long newId) {
            return new ConditionEntity(newId, field, operator, values);
        }
    }

    record BinaryExpressionEntity(Long id, ExpressionEntity left, Expression.BinaryExpression.BinaryOperator operator,
                                  ExpressionEntity right)
            implements ExpressionEntity {

        public BinaryExpressionEntity(ExpressionEntity left, Expression.BinaryExpression.BinaryOperator operator, ExpressionEntity right) {
            this(null, left, operator, right);
        }

        @Override
        public ExpressionType type() {
            return ExpressionType.BINARY;
        }

        @Override
        public BinaryExpressionEntity withId(Long newId) {
            return new BinaryExpressionEntity(newId, left, operator, right);
        }
    }

    record ParenthesizedExpressionEntity(Long id, ExpressionEntity inner)
            implements ExpressionEntity {
        public ParenthesizedExpressionEntity(ExpressionEntity inner) {
            this(null, inner);
        }

        @Override
        public ExpressionType type() {
            return ExpressionType.PARENTHESIZED;
        }

        @Override
        public ParenthesizedExpressionEntity withId(Long newId) {
            return new ParenthesizedExpressionEntity(newId, inner);
        }
    }
}



