package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.CRUD;
import dk.kvalitetsit.klaus.Mapper;
import org.openapitools.model.Expression;
import org.openapitools.model.Prescription;
import org.springframework.stereotype.Service;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 * Note: For now it implements {@link CRUD<Expression>} however this will most likely not be the case in the future
 */
@Service
public class ValidationServiceAdaptor implements ValidationService<Prescription> {

    private final ValidationService<dk.kvalitetsit.klaus.service.model.Prescription> service;
    private final Mapper<Prescription, dk.kvalitetsit.klaus.service.model.Prescription> dtoMapper;
    private final Mapper<dk.kvalitetsit.klaus.service.model.Prescription, Prescription> modelMapper;

    public ValidationServiceAdaptor(
            ValidationService<dk.kvalitetsit.klaus.service.model.Prescription> service,
            Mapper<Prescription, dk.kvalitetsit.klaus.service.model.Prescription> dtoMapper,
            Mapper<dk.kvalitetsit.klaus.service.model.Prescription, Prescription> modelMapper
    ) {
        this.service = service;
        this.dtoMapper = dtoMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean validate(Prescription prescription) {
        return service.validate(this.dtoMapper.map(prescription));
    }
}
