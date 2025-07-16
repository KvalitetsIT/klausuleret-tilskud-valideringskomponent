package dk.kvalitetsit.klaus.integrationtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.ValidationRequest;
import org.springframework.web.client.RestClientResponseException;

public class ValidationIT extends BaseTest  {

    @Test
    void testPostValidation()  {
        var api  =  new ValidationApi(client);
        var request = new ValidationRequest().age(18);
        try {
            var response = api.v1ValidationsPost(request);
            Assertions.assertTrue(response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
