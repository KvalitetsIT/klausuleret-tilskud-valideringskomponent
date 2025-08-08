package dk.kvalitetsit.klaus.integrationtest.api;

import dk.kvalitetsit.klaus.integrationtest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.DslInput;
import org.openapitools.client.model.ExistingDrugMedication;
import org.openapitools.client.model.ValidationRequest;
import org.openapitools.client.model.ValidationStatus;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationIT extends BaseTest {

    private ValidationApi validationApi;
    private ManagementApi managementApi;

    @BeforeEach
    void setup() {
        this.validationApi = new ValidationApi(client);
        this.managementApi = new ManagementApi(client);
    }

    @Test
    void testPostValidation() {
        managementApi.call20250801clausesDslPost(new DslInput().dsl("Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)"));
        var existingDrugMedication = new ExistingDrugMedication()
                .atcCode("C10BA05")
                .drugIdentifier("123456789")
                .formCode("TAB")
                .routeOfAdministrationCode("OK");
        var request = new ValidationRequest()
                .age(18)
                .personIdentifier("1234567890")
                .addExistingDrugMedicationsItem(existingDrugMedication);
        try {
            var response = validationApi.call20250801validatePost(request);
            assertEquals(ValidationStatus.VALIDATED, response.getValidationStatus());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
