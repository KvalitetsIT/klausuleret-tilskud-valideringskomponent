package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.common.model.ClauseField;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;


@ExtendWith(MockitoExtension.class)
class ValidationDataContextMapperTest {

    @InjectMocks
    private  ValidationDataContextMapper mapper;

    @Test
    void map() {
        var indication = "1234";
        var validationInput = new ValidationInput(20, 1L, indication);

        DataContext dataContext = mapper.map(validationInput);

        var expectedDataContext = new DataContext(Map.of(
                ClauseField.AGE.name(), 20,
                ClauseField.INDICATION.name(), indication));
        Assertions.assertEquals(expectedDataContext, dataContext);
    }
}