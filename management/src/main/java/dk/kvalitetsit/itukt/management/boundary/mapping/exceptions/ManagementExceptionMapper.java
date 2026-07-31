package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundApiException;
import dk.kvalitetsit.itukt.management.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.exceptions.InvalidInputException;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;

public class ManagementExceptionMapper implements Mapper<ManagementException, ApiException> {
    private final DslParserExceptionMapper dslParserExceptionMapper;

    public ManagementExceptionMapper(DslParserExceptionMapper dslParserExceptionMapper) {
        this.dslParserExceptionMapper = dslParserExceptionMapper;
    }

    @Override
    public ApiException map(ManagementException managementException) {
        return switch (managementException) {
            case DslParserException dslParserException -> dslParserExceptionMapper.map(dslParserException);
            case NotFoundException invalidInputException -> new NotFoundApiException(invalidInputException.getMessage());
            case InvalidInputException invalidInputException -> new BadRequestApiException(invalidInputException.getMessage());
        };
    }
}
