package dk.kvalitetsit.itukt.service;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.service.model.DataContext;
import org.openapitools.model.ValidationRequest;
import org.springframework.stereotype.Service;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 */
@Service
public class ValidationServiceAdaptor implements ValidationService<ValidationRequest> {

    private final ValidationService<DataContext> service;
    private final Mapper<ValidationRequest, DataContext> mapper;

    public ValidationServiceAdaptor(ValidationService<DataContext> service, Mapper<ValidationRequest, DataContext> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public boolean validate(ValidationRequest request) {
        DataContext ctx = mapper.map(request);
        return service.validate(ctx);
    }
}
