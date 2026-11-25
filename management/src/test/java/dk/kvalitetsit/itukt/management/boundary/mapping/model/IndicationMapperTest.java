package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.model.Indication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndicationMapperTest {
    @Test
    void map_ReturnsMappedIndication() {
        var indication = new Indication(1L, "text");

        var mappedIndication = new IndicationMapper().map(indication);

        var expected = new org.openapitools.model.Indication(indication.code(), indication.text());
        assertEquals(expected, mappedIndication);
    }
}