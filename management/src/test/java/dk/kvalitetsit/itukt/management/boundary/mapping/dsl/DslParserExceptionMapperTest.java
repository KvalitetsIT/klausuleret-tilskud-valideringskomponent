package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DslParserExceptionMapperTest {
    private final DslParserExceptionMapper dslParserExceptionMapper = new DslParserExceptionMapper();

    @Test
    void map_WhenIncompleteDslException_ReturnsBadRequestExceptionWithExpectedMessage() {
        var badRequestException = dslParserExceptionMapper.map(new IncompleteDslException());

        assertEquals("Ufærdig klausulbetingelse", badRequestException.getDetailedError());
    }

    @Test
    void handleDslParserException_WhenUnexpectedValueException_ReturnsBadRequestExceptionWithExpectedMessage() {
        var badRequestException = dslParserExceptionMapper.map(new UnexpectedValueException("abc"));

        assertEquals("Ugyldig klausulbetingelse. Uventet værdi: abc", badRequestException.getDetailedError());
    }

    @Test
    void handleDslParserException_WhenUnexpectedAgeValueException_ReturnsBadRequestExceptionWithExpectedMessage() {
        var badRequestException = dslParserExceptionMapper.map(new UnexpectedAgeValueException("old"));

        assertEquals("Ugyldig klausulbetingelse. Uventet værdi for alder: old", badRequestException.getDetailedError());
    }

    @Test
    void handleDslParserException_WhenUnexpectedEmptyMultiValueConditionException_ReturnsBadRequestExceptionWithExpectedMessage() {
        var badRequestException = dslParserExceptionMapper.map(new UnexpectedEmptyMultiValueConditionException());

        assertEquals("Ugyldig klausulbetingelse. Betingelse med flere værdier må ikke være tom", badRequestException.getDetailedError());
    }

    @Test
    void handleDslParserException_WhenUnexpectedExistingDrugMedicationKeysException_ReturnsBadRequestExceptionWithExpectedMessage() {
        var badRequestException = dslParserExceptionMapper.map(new UnexpectedExistingDrugMedicationKeysException(Set.of("a")));

        assertEquals("Ugyldig klausulbetingelse. Eksisterende lægemiddel kan kun have værdier for: a", badRequestException.getDetailedError());
    }
}