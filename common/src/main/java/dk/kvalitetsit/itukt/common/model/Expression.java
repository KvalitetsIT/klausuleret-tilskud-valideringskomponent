package dk.kvalitetsit.itukt.common.model;


public sealed interface Expression permits Expression.StringCondition, Expression.NumberCondition, Expression.BinaryExpression {

    record StringCondition(String field, String value) implements Expression {
    }

    record NumberCondition(String field, Operator operator, int value) implements Expression {
    }

    record BinaryExpression(Expression left, BinaryOperator operator, Expression right) implements Expression {
        public enum BinaryOperator {OR, AND}
    }

}