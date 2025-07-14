package dk.kvalitetsit.klaus.model;

import java.util.List;
import java.util.UUID;

public sealed interface ExpressionEntity permits ExpressionEntity.ConditionEntity, ExpressionEntity.BinaryExpressionEntity, ExpressionEntity.ParenthesizedExpressionEntity {


    String type();

    record ConditionEntity(String field, String operator, List<String> values) implements ExpressionEntity {
        @Override
        public String type() {
            return "condition_expression";
        }
    }

    record BinaryExpressionEntity(ExpressionEntity left, String operator, ExpressionEntity right) implements ExpressionEntity {
        @Override
        public String type() {
            return "binary_expression";
        }
    }

    record ParenthesizedExpressionEntity(ExpressionEntity inner) implements ExpressionEntity {
        @Override
        public String type() {
            return "parenthesized_expression";
        }
    }
}


