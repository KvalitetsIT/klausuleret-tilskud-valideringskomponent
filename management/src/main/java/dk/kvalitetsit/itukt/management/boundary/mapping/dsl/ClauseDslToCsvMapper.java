package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import org.openapitools.model.DslOutput;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClauseDslToCsvMapper {

    public String map(List<DslOutput> clauses) {
        String header = String.join(";",
                "name",
                "status",
                "dsl",
                "error",
                "createdBy",
                "createdTime");
        return Stream.concat(
                Stream.of(header),
                clauses.stream().map(this::map)
        ).collect(Collectors.joining("\n"));
    }

    private String map(DslOutput clause) {
        return String.join(";",
                clause.getName(),
                clause.getStatus().toString(),
                clause.getDsl(),
                clause.getError(),
                clause.getCreatedBy(),
                clause.getCreatedTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }


}
