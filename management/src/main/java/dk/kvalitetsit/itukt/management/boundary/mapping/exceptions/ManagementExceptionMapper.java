package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.management.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;

public class ManagementExceptionMapper implements Mapper<ManagementException, ApiException> {
    private final DslParserExceptionMapper dslParserExceptionMapper;

    public ManagementExceptionMapper(DslParserExceptionMapper dslParserExceptionMapper) {
        this.dslParserExceptionMapper = dslParserExceptionMapper;
    }

    @Override
    public ApiException map(ManagementException managementException) {
        return switch (managementException) {
            case DslParserException dslParserException -> dslParserExceptionMapper.map(dslParserException);
        };
    }
}
