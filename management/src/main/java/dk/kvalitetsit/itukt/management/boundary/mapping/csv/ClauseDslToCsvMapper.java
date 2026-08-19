package dk.kvalitetsit.itukt.management.boundary.mapping.csv;

import org.openapitools.model.DslOutput;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClauseDslToCsvMapper {
    private final CsvBuilder<DslOutput> csvBuilder = new CsvBuilder<DslOutput>()
            .column("name", DslOutput::getName)
            .column("status", c -> c.getStatus().toString())
            .column("dsl", DslOutput::getDsl)
            .column("error", DslOutput::getError)
            .column("createdBy", DslOutput::getCreatedBy)
            .column("createdTime", c ->
                    c.getCreatedTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

    public String map(List<DslOutput> clauses) {
        return csvBuilder.build(clauses);
    }
}
