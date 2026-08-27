package dk.kvalitetsit.itukt.management.boundary.mapping.validation;

import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownFormCodeError;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionValidationErrorMapperTest {

    @Test
    void map_WithUnknownDepartmentSpecialityError_MapsError() {
        var mapper = new ExpressionValidationErrorMapper();
        var speciality = "test";
        var error = new UnknownDepartmentSpecialityError(speciality, Set.of());

        String errorMessage = mapper.map(error);

        assertEquals("Ukendt afdelingsspeciale " + speciality, errorMessage);
    }

    @Test
    void map_WithUnknownFormCodeError_MapsError() {
        var mapper = new ExpressionValidationErrorMapper();
        var formCode = "test";
        var error = new UnknownFormCodeError(formCode, Set.of());

        String errorMessage = mapper.map(error);

        assertEquals("Ukendt formkode " + formCode, errorMessage);
    }
}