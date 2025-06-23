package dk.kvalitetsit.hello.integrationtest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.model.DetailedError;

import static dk.kvalitetsit.hello.integrationtest.IntegrationTest.api;
import static org.junit.jupiter.api.Assertions.*;

import org.openapitools.client.model.HelloRequest;

class HelloIT {
    static final String input = "Some Name";

    @BeforeAll
    static void setup() {
        try {
            api.v1HelloPost(new HelloRequest().name(input));
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCallServiceWithName() throws ApiException {
        var result = api.v1HelloGet(input);

        assertNotNull(result);
        assertEquals(1, result.size());
        boolean containsSomeName = result.stream()
                .anyMatch(dbEntry -> input.equals(dbEntry.getName()));
        assert (containsSomeName);
    }

    @Test
    void testCallServiceWithNameNotInDB() throws ApiException {
        //Test that calling the get method with a name that is not in the db returns an empty list
        var input = "notindb";

        var result = api.v1HelloGet(input);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testCallServiceWithoutName() throws ApiException {
        String input = null;

        var result = api.v1HelloGet(input);

        assertNotNull(result);
        assertEquals(1, result.size());
        boolean containsSomeName = result.stream()
                .anyMatch(dbEntry -> HelloIT.input.equals(dbEntry.getName()));
        assert (containsSomeName);
    }

    @Test
    void testCallPostService() throws ApiException {
        var request = new HelloRequest().name("John Dow");

        var postResult = api.v1HelloPost(request);
        assertNotNull(postResult);
        assertEquals(request.getName(), postResult.getName());
        assertNull(postResult.getiCanBeNull());
        assertNotNull(postResult.getNow());
    }

    @Test
    void testCallGetServiceNameTooLong() {
        var input = "John Doe Is Too Long";

        var thrownException = assertThrows(ApiException.class, () -> api.v1HelloGet(input));
        DetailedError detailedError = JSON.deserialize(thrownException.getResponseBody(), DetailedError.class);
        assertEquals("Bad Request", detailedError.getError());
        assertEquals("/v1/hello", detailedError.getPath());
        assertEquals("v1HelloGet.name: size must be between 0 and 10", detailedError.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, detailedError.getDetailedErrorCode());
        assertNotNull(detailedError.getTimestamp());
        assertEquals(400, detailedError.getStatus().longValue());
    }

    @Test
    void testCallPostServiceNameTooLong() {
        var request = new HelloRequest().name("John Doe Is Too Long");

        var thrownException = assertThrows(ApiException.class, () -> api.v1HelloPost(request));
        DetailedError detailedError = JSON.deserialize(thrownException.getResponseBody(), DetailedError.class);
        assertEquals("Bad Request", detailedError.getError());
        assertEquals("/v1/hello", detailedError.getPath());
        assertEquals("name: size must be between 0 and 10", detailedError.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, detailedError.getDetailedErrorCode());
        assertNotNull(detailedError.getTimestamp());
        assertEquals(400, detailedError.getStatus().longValue());
    }

    @Test
    void testCallServiceNameValidationError() {
        var input = "NOT_VALID";

        var thrownException = assertThrows(ApiException.class, () -> api.v1HelloGet(input));
        assertEquals(400, thrownException.getCode());

        DetailedError detailedError = JSON.deserialize(thrownException.getResponseBody(), DetailedError.class);
        assertEquals("Bad Request", detailedError.getError());
        assertEquals("/v1/hello", detailedError.getPath());
        assertEquals("NOT_VALID is not a valid name.", detailedError.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, detailedError.getDetailedErrorCode());
        assertNotNull(detailedError.getTimestamp());
        assertEquals(400, detailedError.getStatus().longValue());
    }
}