package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringToDslValueMapperTest {
    private final StringToDslValueMapper stringToDslValueMapper = new StringToDslValueMapper();

    @Test
    void map_WithWildcard_ReturnsWildcard() {
        String wildcard = "*";

        String mappedValue = stringToDslValueMapper.map(wildcard);

        assertEquals(wildcard, mappedValue);
    }

    @Test
    void map_WithNonWildcard_ReturnsValueWithQuotes() {
        String value = "testValue";

        String mappedValue = stringToDslValueMapper.map(value);

        assertEquals("\"" + value + "\"", mappedValue);
    }
}