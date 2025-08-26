package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.DslInput;
import org.openapitools.client.model.ExistingDrugMedication;
import org.openapitools.client.model.ValidationRequest;
import org.openapitools.client.model.ValidationSuccess;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ValidationIT extends BaseTest {

    private ValidationApi validationApi;
    private ManagementApi managementApi;

    @BeforeEach
    void setup() {
        this.validationApi = new ValidationApi(client);
        this.managementApi = new ManagementApi(client);
    }

    @Test
    void testPostValidation() {
        managementApi.call20250801clausesDslPost(new DslInput().dsl(MockFactory.CLAUSE_1_DSL));
        var existingDrugMedication = new ExistingDrugMedication()
                .atcCode("C10BA05")
                .drugIdentifier(123456789L)
                .formCode("TAB")
                .routeOfAdministrationCode("OK");
        var request = new ValidationRequest()
                .age(18)
                .personIdentifier("1234567890")
                .addExistingDrugMedicationsItem(existingDrugMedication);
        try {
            var response = validationApi.call20250801validatePost(request);
            assertInstanceOf(ValidationSuccess.class, response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
