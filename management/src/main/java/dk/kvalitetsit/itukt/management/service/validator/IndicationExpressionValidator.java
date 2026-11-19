package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.common.model.IndicationConditionExpression;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;

public class IndicationExpressionValidator implements ExpressionValidator<IndicationConditionExpression> {
    private final StamdataCacheService<Indication> indicationService;

    public IndicationExpressionValidator(StamdataCacheService<Indication> indicationService) {
        this.indicationService = indicationService;
    }

    @Override
    public List<ExpressionValidationError> validate(IndicationConditionExpression expression) {
        var indication = indicationService.get(expression.requiredValue());
        return indication.isPresent() ? List.of()
                : List.of(
                new UnknownValueError(
                        Identifier.INDICATION,
                        expression.requiredValue()
                ));
    }
}
