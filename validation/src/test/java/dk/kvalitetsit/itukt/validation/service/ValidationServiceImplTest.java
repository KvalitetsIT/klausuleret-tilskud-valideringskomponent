package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl service;
    @Mock
    private ClauseCache clauseCache;

    @Test
    void testValidate() {
        var validationInput = new ValidationInput(5, "1234");

        var result = service.validate(validationInput);

        assertInstanceOf(ValidationSuccess.class, result);
    }

}
