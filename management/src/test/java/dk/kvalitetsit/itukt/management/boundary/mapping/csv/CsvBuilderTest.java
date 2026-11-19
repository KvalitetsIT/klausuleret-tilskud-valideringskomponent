package dk.kvalitetsit.itukt.management.boundary.mapping.csv;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvBuilderTest {

    @Test
    void build_WithNoColumns_ReturnsEmpty() {
        var csvBuilder = new CsvBuilder<String>();

        String csv = csvBuilder.build(List.of("a", "b", "c"));

        assertTrue(csv.isBlank());
    }

    @Test
    void build_WithColumnsButEmptyInputList_ReturnsHeaders() {
        var csvBuilder = new CsvBuilder<String>();
        csvBuilder.column("A", s -> s);
        csvBuilder.column("B", s -> s);

        String csv = csvBuilder.build(List.of());

        assertEquals("A;B", csv);
    }

    @Test
    void build_WithColumns_ReturnsCsv() {
        var csvBuilder = new CsvBuilder<Integer>();
        csvBuilder.column("A", i -> "A" + i);
        csvBuilder.column("B", i -> "B" + i);

        String csv = csvBuilder.build(List.of(1, 2, 3));

        String expectedCsv = String.join("\n",
                "A;B",
                "A1;B1",
                "A2;B2",
                "A3;B3");
        assertEquals(expectedCsv, csv);
    }
}