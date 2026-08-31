package dk.kvalitetsit.itukt.management.boundary.mapping.validation;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionValidationErrorMapperTest {

    @Test
    void map_WithUnknownValueError_MapsError() {
        var mapper = new ExpressionValidationErrorMapper();
        var value = "test";
        var error = new UnknownValueError(Identifier.DEPARTMENT_SPECIALITY, value, Set.of("existing1", "existing2"));

        String errorMessage = mapper.map(error);

        assertEquals("Ukendt afdelingsspeciale 'test'.", errorMessage);
    }
}