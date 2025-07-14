package dk.kvalitetsit.klaus.model;

import java.util.List;

public sealed interface Expression permits Expression.Condition, Expression.BinaryExpression, Expression.ParenthesizedExpression  {

    record Condition(String field, String operator, List<String> values) implements Expression { }

    record BinaryExpression(Expression left, String operator, Expression right) implements Expression { }

    record ParenthesizedExpression(Expression inner) implements Expression { }
}