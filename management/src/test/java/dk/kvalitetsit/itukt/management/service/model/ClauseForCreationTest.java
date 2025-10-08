package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClauseForCreationTest {

    @Test
    void givenClauseWithNullExpression_whenCreate_thenThrow() {
        var e = Assertions.assertThrows(IllegalArgumentException.class, () -> new ClauseForCreation("blaah", null), "Expected exception since expression is null");
        assertEquals("Expected an expression but was null", e.getMessage());
    }

    @Test
    void givenClauseWithNullName_whenCreate_thenThrow() {
        var e = Assertions.assertThrows(IllegalArgumentException.class, () -> new ClauseForCreation(null, new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "blaah")), "Expected exception since expression is null");
        assertEquals("Expected a name but was null", e.getMessage());
    }

}