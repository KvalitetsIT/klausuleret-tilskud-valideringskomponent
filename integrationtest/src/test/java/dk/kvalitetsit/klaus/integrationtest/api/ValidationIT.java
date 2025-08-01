package dk.kvalitetsit.klaus.integrationtest.api;

import dk.kvalitetsit.klaus.integrationtest.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.ExistingDrugMedication;
import org.openapitools.client.model.ValidationRequest;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

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
        managementApi.call20250801clausesDslPost(List.of("Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)"));
        var request = new ValidationRequest().age(18).addExistingDrugMedicationsItem(new ExistingDrugMedication().atcCode("C10BA05"));
        try {
            var response = validationApi.call20250801validationsPost(request);
            Assertions.assertTrue(response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
