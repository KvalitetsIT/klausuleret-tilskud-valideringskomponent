package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.CRUD;
import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.service.model.DataContext;
import org.openapitools.model.Expression;
import org.openapitools.model.ValidationRequest;
import org.springframework.stereotype.Service;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 * Note: For now it implements {@link CRUD<Expression>} however this will most likely not be the case in the future
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
