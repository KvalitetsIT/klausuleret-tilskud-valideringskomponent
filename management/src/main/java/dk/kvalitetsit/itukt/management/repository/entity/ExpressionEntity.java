package dk.kvalitetsit.itukt.management.repository.entity;

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

        @Override
        public ExpressionType type() {
            return ExpressionType.CONDITION;
        }

        @Override
        public ConditionEntity withId(Long newId) {
            return new ConditionEntity(newId, field, operator, values);
        }
    }

    record BinaryExpressionEntity(Long id, ExpressionEntity left, String operator, ExpressionEntity right)
            implements ExpressionEntity {

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



