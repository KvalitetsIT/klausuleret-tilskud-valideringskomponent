package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import org.junit.jupiter.api.Test;
import org.openapitools.model.ClauseStatus;
import org.openapitools.model.DslOutput;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClauseDslToCsvMapperTest {
    private final ClauseDslToCsvMapper mapper = new ClauseDslToCsvMapper();

    @Test
    void map_WithEmptyList_ReturnsHeader() {
        String csv = mapper.map(List.of());

        assertEquals("name;status;dsl;error;createdBy;createdTime", csv);
    }

    @Test
    void map_WithClauseEntries_ReturnsClausesAsCsv() {
        var clause1 = new DslOutput("Clause1", "Error1", "DSL1", UUID.randomUUID(), ClauseStatus.ACTIVE, "User1", OffsetDateTime.now());
        var clause2 = new DslOutput("Clause2", "Error2", "DSL2", UUID.randomUUID(), ClauseStatus.INACTIVE, "User2", OffsetDateTime.now());

        String csv = mapper.map(List.of(clause1, clause2));

        String expectedCsv = String.join("\n",
                "name;status;dsl;error;createdBy;createdTime",
                "Clause1;ACTIVE;DSL1;Error1;User1;" + clause1.getCreatedTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "Clause2;INACTIVE;DSL2;Error2;User2;" + clause2.getCreatedTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        assertEquals(expectedCsv, csv);
    }
}