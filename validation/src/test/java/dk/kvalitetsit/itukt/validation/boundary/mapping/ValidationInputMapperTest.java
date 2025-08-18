package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.NewDrugMedication;
import org.openapitools.model.Validate;
import org.openapitools.model.ValidationRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationInputMapperTest {

    private ValidationInputMapper validationInputMapper;

    @BeforeEach
    void setUp() {
        validationInputMapper = new ValidationInputMapper();
    }

    @Test
    void map_WithNoValidateItems_ReturnsEmptyList() {
        var validationRequest = new ValidationRequest()
                .age(5);

        var validationInputs = validationInputMapper.map(validationRequest);

        assertTrue(validationInputs.isEmpty());
    }

    @Test
    void map_WithMultipleValidateItems_MapsAgeAndDrugId() {
        var newDrugMedication1 = new NewDrugMedication()
                .drugIdentifier(11111L);
        var validate1 = new Validate()
                .newDrugMedication(newDrugMedication1);
        var newDrugMedication2 = new NewDrugMedication()
                .drugIdentifier(22222L);
        var validate2 = new Validate()
                .newDrugMedication(newDrugMedication2);
        var validationRequest = new ValidationRequest()
                .age(5)
                .addValidateItem(validate1)
                .addValidateItem(validate2);

        var validationInputs = validationInputMapper.map(validationRequest);

        assertEquals(2, validationInputs.size());
        var expectedInput1 = new ValidationInput(validationRequest.getAge(), newDrugMedication1.getDrugIdentifier());
        var expectedInput2 = new ValidationInput(validationRequest.getAge(), newDrugMedication2.getDrugIdentifier());
        assertTrue(validationInputs.contains(expectedInput1));
        assertTrue(validationInputs.contains(expectedInput2));
    }
}