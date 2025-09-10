package dk.kvalitetsit.itukt.common.model;


public sealed interface Expression permits Expression.Condition, Expression.BinaryExpression {

    record Condition(String field, Operator operator, String value) implements Expression {
    }

    record BinaryExpression(Expression left, BinaryOperator operator, Expression right) implements Expression {
        public enum BinaryOperator {OR, AND}
    }

}