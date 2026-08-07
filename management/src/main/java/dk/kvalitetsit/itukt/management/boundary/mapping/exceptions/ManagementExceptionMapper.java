package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundApiException;
import dk.kvalitetsit.itukt.management.exceptions.*;

public class ManagementExceptionMapper implements Mapper<ManagementException, ApiException> {
    private final DslParserExceptionMapper dslParserExceptionMapper;

    public ManagementExceptionMapper(DslParserExceptionMapper dslParserExceptionMapper) {
        this.dslParserExceptionMapper = dslParserExceptionMapper;
    }

    @Override
    public ApiException map(ManagementException managementException) {
        return switch (managementException) {
            case DslParserException e -> dslParserExceptionMapper.map(e);
            case NotFoundException e -> new NotFoundApiException(e.getMessage());
            case InvalidInputException e -> new BadRequestApiException(e.getMessage());
            case RequiresForceException e -> new BadRequestApiException(e.getMessage());
        };
    }
}
