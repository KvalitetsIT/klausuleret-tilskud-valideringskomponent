package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.repository.entity.Field;
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
            .dsl("Klausul CHOL: (INDIKATION = C10BA03) eller (INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)")
            .error("message");
    private static final StringConditionEntity EXPRESSION_2_ENTITY = new StringConditionEntity(2L, Field.INDICATION, "C10BA03");
    private static final StringConditionEntity EXPRESSION_3_ENTITY = new StringConditionEntity(3L, Field.INDICATION, "C10BA02");
    private static final StringConditionEntity EXPRESSION_4_ENTITY = new StringConditionEntity(4L, Field.INDICATION, "C10BA05");
    private static final BinaryExpressionEntity EXPRESSION_5_ENTITY = new BinaryExpressionEntity(EXPRESSION_3_ENTITY, BinaryExpression.Operator.OR, EXPRESSION_4_ENTITY);
    private static final NumberConditionEntity EXPRESSION_6_ENTITY = new NumberConditionEntity(6L, Field.AGE, GREATER_THAN_OR_EQUAL_TO, 13);
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
    private static final org.openapitools.client.model.AgeCondition EXPRESSION_6_DTO = new org.openapitools.client.model.AgeCondition().type(ExpressionType.AGE)
            .operator(org.openapitools.client.model.Operator.GREATER_THAN_OR_EQUAL_TO)
            .value(EXPRESSION_6_ENTITY.value());
    private static final org.openapitools.client.model.IndicationCondition EXPRESSION_3_DTO = new org.openapitools.client.model.IndicationCondition().type("IndicationCondition")
            .value((EXPRESSION_3_ENTITY.value()));
    private static final org.openapitools.client.model.IndicationCondition EXPRESSION_4_DTO = new org.openapitools.client.model.IndicationCondition().type(ExpressionType.INDICATION)
            .value(EXPRESSION_4_ENTITY.value());
    private static final org.openapitools.client.model.BinaryExpression EXPRESSION_5_DTO = new org.openapitools.client.model.BinaryExpression()
            .type(ExpressionType.BINARY)
            .left(EXPRESSION_3_DTO)
            .operator(org.openapitools.client.model.BinaryOperator.fromValue(EXPRESSION_5_ENTITY.operator().name()))
            .right(EXPRESSION_4_DTO);
    private static final org.openapitools.client.model.IndicationCondition EXPRESSION_2_DTO = new org.openapitools.client.model.IndicationCondition()
            .type(ExpressionType.INDICATION)
            .value((EXPRESSION_2_ENTITY.value()));
    private static final org.openapitools.client.model.BinaryExpression EXPRESSION_7_DTO = new org.openapitools.client.model.BinaryExpression()
            .type(ExpressionType.BINARY)
            .left(EXPRESSION_5_DTO)
            .operator(BinaryOperator.AND)
            .right(EXPRESSION_6_DTO);
    private static final org.openapitools.client.model.Expression EXPRESSION_1_DTO = new org.openapitools.client.model.BinaryExpression()
            .type(ExpressionType.BINARY)
            .left(EXPRESSION_2_DTO)
            .operator(org.openapitools.client.model.BinaryOperator.OR)
            .right(EXPRESSION_7_DTO);
    public static final ClauseOutput CLAUSE_1_OUTPUT = new ClauseOutput()
            .name("CHOL")
            .expression(EXPRESSION_1_DTO)
            .uuid(UUID.randomUUID()).error("message");

    public static final ClauseInput CLAUSE_1_INPUT = new ClauseInput()
            .name("CHOL")
            .expression(EXPRESSION_1_DTO)
            .error("message");

    public static org.openapitools.client.model.ExistingDrugMedicationCondition createExistingDrugMedicationCondition(
            String atcCode,
            String formCode,
            String routeOfAdministrationCode
    ) {
        return new org.openapitools.client.model.ExistingDrugMedicationCondition()
                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                .atcCode(atcCode)
                .formCode(formCode)
                .routeOfAdministrationCode(routeOfAdministrationCode);
    }

    public static org.openapitools.client.model.BinaryExpression createBinaryAndExpression(
            org.openapitools.client.model.Expression left,
            org.openapitools.client.model.Expression right
    ) {
        return new org.openapitools.client.model.BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(left)
                .operator(BinaryOperator.AND)
                .right(right);
    }
}