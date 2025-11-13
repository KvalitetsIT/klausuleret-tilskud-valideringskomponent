package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExpressionDtoDslMapper implements Mapper<Expression, String> {

    private final ExpressionDslMapper<IndicationCondition> indicationConditionExpressionDslMapper;
    private final ExpressionDslMapper<AgeCondition> ageConditionExpressionDslMapper;
    private final Mapper<BinaryExpression, Dsl> binaryExpressionExpressionDslMapper;
    private final ExpressionDslMapper<ExistingDrugMedicationCondition> existingDrugMedicationConditionExpressionDslMapper;

    public ExpressionDtoDslMapper(MapperFactory factory) {
        existingDrugMedicationConditionExpressionDslMapper = factory.getExistingDrugMedicationConditionExpressionDslMapper();
        binaryExpressionExpressionDslMapper = factory.getBinaryExpressionExpressionDslMapper(this);
        ageConditionExpressionDslMapper = factory.getAgeConditionExpressionDslMapper();
        indicationConditionExpressionDslMapper = factory.getIndicationConditionExpressionDslMapper();
    }

    private static <T extends Expression> List<T> castList(List<?> list, Class<T> clazz) {
        return list.stream()
                .map(clazz::cast)
                .toList();
    }

    protected static <T extends Expression> String mergeConditions(Identifier identifier, List<T> conditions, Function<T, String> supplier) {
        String joined = conditions.stream()
                .map(supplier)
                .collect(Collectors.joining(", "));
        return identifier + " i [" + joined + "]";
    }

    @Override
    public String map(Expression entry) {
        return toDsl(entry).dsl();
    }

    protected Dsl toDsl(Expression entry) {
        return switch (entry) {
            case BinaryExpression b -> binaryExpressionExpressionDslMapper.map(b);
            case AgeCondition ageCondition -> ageConditionExpressionDslMapper.map(ageCondition);
            case ExistingDrugMedicationCondition existingDrugMedicationCondition ->
                    existingDrugMedicationConditionExpressionDslMapper.map(existingDrugMedicationCondition);
            case IndicationCondition indicationCondition ->
                    indicationConditionExpressionDslMapper.map(indicationCondition);
            case DoctorSpecialityCondition doctorSpecialityCondition -> throw new RuntimeException("TODO");
        };
    }

    protected String mergeConditions(List<? extends Expression> conditions) {
        if (conditions.isEmpty()) throw new IllegalStateException("Expected at least a single condition, but none were given");
        if (conditions.size() == 1) return this.map(conditions.getFirst());

        return switch (conditions.getFirst()) {
            case AgeCondition ignored ->
                    ageConditionExpressionDslMapper.merge(castList(conditions, AgeCondition.class));
            case ExistingDrugMedicationCondition ignored ->
                    existingDrugMedicationConditionExpressionDslMapper.merge(castList(conditions, ExistingDrugMedicationCondition.class));
            case IndicationCondition ignored ->
                    indicationConditionExpressionDslMapper.merge(castList(conditions, IndicationCondition.class));
            default -> throw new IllegalStateException("Unexpected value: " + conditions.getFirst());
        };
    }
}
