package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.BinaryExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.NumberConditionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity.StringConditionEntity;
import org.openapitools.client.model.BinaryOperator;
import org.openapitools.client.model.ClauseInput;
import org.openapitools.client.model.ClauseOutput;
import org.openapitools.client.model.DslInput;

import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

public class MockFactory {

    // Note: This clause(clause_1_dsl) matches: clause_1_*
    public static final DslInput CLAUSE_1_DSL_INPUT = new DslInput()
            .dsl("Klausul CHOL: (INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)")
            .error(new org.openapitools.client.model.Error().message("message"));
    private static final StringConditionEntity EXPRESSION_2_ENTITY = new StringConditionEntity(2L, Expression.Condition.Field.INDICATION, "C10BA03");
    private static final StringConditionEntity EXPRESSION_3_ENTITY = new StringConditionEntity(3L, Expression.Condition.Field.INDICATION, "C10BA02");
    private static final StringConditionEntity EXPRESSION_4_ENTITY = new StringConditionEntity(4L, Expression.Condition.Field.INDICATION, "C10BA05");
    private static final BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(EXPRESSION_3_ENTITY, BinaryExpression.Operator.OR, EXPRESSION_4_ENTITY);
    private static final NumberConditionEntity EXPRESSION_6_ENTITY = new NumberConditionEntity(6L, Expression.Condition.Field.AGE, GREATER_THAN_OR_EQUAL_TO, 13);
    private static final BinaryExpressionEntity EXPRESSION_7_ENTITY = new BinaryExpressionEntity(
            5L,
            EXPRESSION_5_ENTITY,
            BinaryExpression.Operator.AND,
            EXPRESSION_6_ENTITY
    );
    public static final ExpressionEntity EXPRESSION_1_ENTITY = new BinaryExpressionEntity(
            1L,
            EXPRESSION_2_ENTITY,
            BinaryExpression.Operator.OR,
            EXPRESSION_7_ENTITY
    );
    // Note: This clause(clause_1_entity) matches: clause_1_dsl
    public static ClauseEntity CLAUSE_1_ENTITY = new ClauseEntity(null, UUID.randomUUID(), "CHOL", 0, "message", EXPRESSION_1_ENTITY);
    private static final NumberConditionExpression EXPRESSION_6_MODEL = new NumberConditionExpression(
            EXPRESSION_6_ENTITY.field(),
            EXPRESSION_6_ENTITY.operator(),
            EXPRESSION_6_ENTITY.value()
    );
    private static final org.openapitools.client.model.NumberCondition EXPRESSION_6_DTO = new org.openapitools.client.model.NumberCondition().type("NumberCondition")
            .field(EXPRESSION_6_MODEL.field().name())
            .operator(org.openapitools.client.model.Operator.GREATER_THAN_OR_EQUAL_TO)
            .value(EXPRESSION_6_MODEL.value());
    private static final StringConditionExpression EXPRESSION_3_MODEL = new StringConditionExpression(
            EXPRESSION_3_ENTITY.field(),
            EXPRESSION_3_ENTITY.value());
    private static final org.openapitools.client.model.StringCondition EXPRESSION_3_DTO = new org.openapitools.client.model.StringCondition().type("StringCondition")
            .field(EXPRESSION_3_MODEL.field().name())
            .value((EXPRESSION_3_MODEL.requiredValue()));
    private static final StringConditionExpression EXPRESSION_4_MODEL = new StringConditionExpression(
            EXPRESSION_4_ENTITY.field(),
            EXPRESSION_4_ENTITY.value());
    private static final BinaryExpression EXPRESSION_5_MODEL = new BinaryExpression(
            EXPRESSION_3_MODEL,
            EXPRESSION_5_ENTITY.operator(),
            EXPRESSION_4_MODEL);
    private static final org.openapitools.client.model.StringCondition EXPRESSION_4_DTO = new org.openapitools.client.model.StringCondition().type("StringCondition")
            .field(EXPRESSION_4_MODEL.field().name())
            .value(EXPRESSION_4_MODEL.requiredValue());
    private static final org.openapitools.client.model.BinaryExpression EXPRESSION_5_DTO = new org.openapitools.client.model.BinaryExpression()
            .type("BinaryExpression")
            .left(EXPRESSION_3_DTO)
            .operator(org.openapitools.client.model.BinaryOperator.fromValue(EXPRESSION_5_MODEL.operator().name()))
            .right(EXPRESSION_4_DTO);
    private static final StringConditionExpression EXPRESSION_2_MODEL = new StringConditionExpression(
            EXPRESSION_2_ENTITY.field(),
            EXPRESSION_2_ENTITY.value()
    );
    private static final BinaryExpression EXPRESSION_1_MODEL = new BinaryExpression(
            EXPRESSION_2_MODEL,
            BinaryExpression.Operator.OR,
            new BinaryExpression(
                    EXPRESSION_5_MODEL,
                    BinaryExpression.Operator.AND,
                    EXPRESSION_6_MODEL
            )
    );
    public static final Clause CLAUSE_1_MODEL = new Clause(1L, CLAUSE_1_ENTITY.name(), CLAUSE_1_ENTITY.uuid(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL);
    private static final org.openapitools.client.model.StringCondition EXPRESSION_2_DTO = new org.openapitools.client.model.StringCondition()
            .type("StringCondition")
            .field(EXPRESSION_2_MODEL.field().name())
            .value((EXPRESSION_2_MODEL.requiredValue()));
    private static final org.openapitools.client.model.Expression EXPRESSION_1_DTO = new org.openapitools.client.model.BinaryExpression()
            .type("BinaryExpression")
            .operator(org.openapitools.client.model.BinaryOperator.OR)
            .left(EXPRESSION_2_DTO)
            .right(new org.openapitools.client.model.BinaryExpression()
                    .type("BinaryExpression")
                    .left(EXPRESSION_5_DTO)
                    .operator(org.openapitools.client.model.BinaryOperator.AND)
                    .right(EXPRESSION_6_DTO)
            );
    public static final ClauseOutput CLAUSE_1_OUTPUT = new ClauseOutput()
            .name("CHOL")
            .expression(EXPRESSION_1_DTO)
            .uuid(CLAUSE_1_MODEL.uuid()).error(new org.openapitools.client.model.Error().message("message"));

    public static final ClauseInput CLAUSE_1_INPUT = new ClauseInput()
            .name("CHOL")
            .expression(EXPRESSION_1_DTO)
            .error(new org.openapitools.client.model.Error().message("message"));

    public static org.openapitools.client.model.ExistingDrugMedicationCondition createExistingDrugMedicationCondition(
            String atcCode,
            String formCode,
            String routeOfAdministrationCode
    ) {
        return new org.openapitools.client.model.ExistingDrugMedicationCondition()
                .type("ExistingDrugMedicationCondition")
                .atcCode(atcCode)
                .formCode(formCode)
                .routeOfAdministrationCode(routeOfAdministrationCode);
    }

    public static org.openapitools.client.model.BinaryExpression createBinaryAndExpression(
            org.openapitools.client.model.Expression left,
            org.openapitools.client.model.Expression right
    ) {
        return new org.openapitools.client.model.BinaryExpression()
                .type("BinaryExpression")
                .left(left)
                .operator(BinaryOperator.AND)
                .right(right);
    }

}

