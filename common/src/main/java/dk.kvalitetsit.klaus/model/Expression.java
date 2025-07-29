package dk.kvalitetsit.klaus.model;


import java.util.List;

public sealed interface Expression permits Expression.Condition, Expression.BinaryExpression, Expression.ParenthesizedExpression {

    record Condition(String field, Operator operator, List<String> values) implements Expression {
    }

    record BinaryExpression(Expression left, BinaryOperator operator, Expression right) implements Expression {
        public enum BinaryOperator {
            OR("OR"),
            AND("AND");

            private final String value;

            BinaryOperator(String value) {
                this.value = value;
            }

            public static BinaryOperator fromValue(String value) {
                for (BinaryOperator op : BinaryOperator.values()) {
                    if (op.getValue().equals(value)) {
                        return op;
                    }
                }
                throw new IllegalArgumentException("Unknown value: " + value);
            }

            public String getValue() {
                return value;
            }

        }
    }

    record ParenthesizedExpression(Expression inner) implements Expression {
    }


}