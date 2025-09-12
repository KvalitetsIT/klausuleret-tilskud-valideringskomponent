package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;

public sealed interface ExpressionEntity permits
        ExpressionEntity.StringConditionEntity,
        ExpressionEntity.NumberConditionEntity,
        ExpressionEntity.BinaryExpressionEntity {

    ExpressionType type();

    Long id();

    ExpressionEntity withId(Long newId);

    record StringConditionEntity(Long id, Expression.Condition.Field field, String value)
            implements ExpressionEntity {

        public StringConditionEntity(Expression.Condition.Field field, String value) {
            this(null, field, value);
        }

        @Override
        public ExpressionType type() {
            return ExpressionType.STRING_CONDITION;
        }

        @Override
        public StringConditionEntity withId(Long newId) {
            return new StringConditionEntity(newId, field, value);
        }
    }

    record NumberConditionEntity(Long id, Expression.Condition.Field field, Operator operator, int value)
            implements ExpressionEntity {

        public NumberConditionEntity(Expression.Condition.Field field, Operator operator, int value) {
            this(null, field, operator, value);
        }

        @Override
        public ExpressionType type() {
            return ExpressionType.NUMBER_CONDITION;
        }

        @Override
        public NumberConditionEntity withId(Long newId) {
            return new NumberConditionEntity(newId, field, operator, value);
        }
    }

    record BinaryExpressionEntity(Long id, ExpressionEntity left, BinaryExpression.Operator operator,
                                  ExpressionEntity right)
            implements ExpressionEntity {

        public BinaryExpressionEntity(ExpressionEntity left, BinaryExpression.Operator operator, ExpressionEntity right) {
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

}



