package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.management.exceptions.UnexpectedValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ManagementExceptionMapperTest {
    @Mock
    private DslParserExceptionMapper dslParserExceptionMapper;

    @InjectMocks
    private ManagementExceptionMapper managementExceptionMapper;

    @Test
    void map_WithDslParserException() {
        var dslParserException = new UnexpectedValueException("message");
        var badRequestException = new BadRequestException("message");
        Mockito.when(dslParserExceptionMapper.map(dslParserException)).thenReturn(badRequestException);

        var result = managementExceptionMapper.map(dslParserException);

        assertEquals(badRequestException, result);
    }
}