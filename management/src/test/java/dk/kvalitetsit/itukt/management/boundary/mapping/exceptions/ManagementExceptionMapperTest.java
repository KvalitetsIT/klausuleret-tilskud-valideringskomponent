package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundApiException;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.exceptions.UnexpectedValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class ManagementExceptionMapperTest {
    @Mock
    private DslParserExceptionMapper dslParserExceptionMapper;

    @InjectMocks
    private ManagementExceptionMapper managementExceptionMapper;

    @Test
    void map_WithDslParserException() {
        var dslParserException = new UnexpectedValueException("message");
        var badRequestException = new BadRequestApiException("message");
        Mockito.when(dslParserExceptionMapper.map(dslParserException)).thenReturn(badRequestException);

        var result = managementExceptionMapper.map(dslParserException);

        assertEquals(badRequestException, result);
    }

    @Test
    void map_WithNotFoundException() {
        var inputException = new NotFoundException("message");

        var result = managementExceptionMapper.map(inputException);

        var apiException = assertInstanceOf(NotFoundApiException.class, result);
        assertEquals(inputException.getMessage(), apiException.getDetailedError());
    }
}