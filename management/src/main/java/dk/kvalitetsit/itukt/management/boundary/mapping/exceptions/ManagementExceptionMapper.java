package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import dk.kvalitetsit.itukt.common.exceptions.ExpressionValidationApiException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundApiException;
import dk.kvalitetsit.itukt.management.exceptions.*;

public class ManagementExceptionMapper implements Mapper<ManagementException, ApiException> {
    private final DslParserExceptionMapper dslParserExceptionMapper;
    private final Mapper<ExpressionValidationException, ExpressionValidationApiException> expressionValidationExceptionMapper;

    public ManagementExceptionMapper(DslParserExceptionMapper dslParserExceptionMapper, Mapper<ExpressionValidationException, ExpressionValidationApiException> expressionValidationExceptionMapper) {
        this.dslParserExceptionMapper = dslParserExceptionMapper;
        this.expressionValidationExceptionMapper = expressionValidationExceptionMapper;
    }

    @Override
    public ApiException map(ManagementException managementException) {
        return switch (managementException) {
            case DslParserException e -> dslParserExceptionMapper.map(e);
            case NotFoundException e -> new NotFoundApiException(e.getMessage());
            case InvalidInputException e -> new BadRequestApiException(e.getMessage());
            case ExpressionValidationException e -> expressionValidationExceptionMapper.map(e);
        };
    }
}
