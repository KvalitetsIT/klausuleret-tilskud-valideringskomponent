package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ValidationRequest;

import java.util.List;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class ValidationDataContextMapperTest {

    @InjectMocks
    private  ValidationDataContextMapper mapper;

    @Test
    void map() {
        var request = new ValidationRequest().age(20);
        var ctx = new DataContext(Map.of("ALDER", List.of("20")));
        Assertions.assertEquals(ctx, mapper.map(request));
    }
}