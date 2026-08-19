package dk.kvalitetsit.itukt.management.boundary.mapping.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvBuilder<T> {

    private final List<String> headers = new ArrayList<>();
    private final List<Function<T, String>> columns = new ArrayList<>();

    public CsvBuilder<T> column(String header, Function<T, String> mapper) {
        headers.add(header);
        columns.add(mapper);
        return this;
    }

    public String build(List<T> items) {
        return Stream.concat(
                Stream.of(String.join(";", headers)),
                items.stream().map(this::mapRow)
        ).collect(Collectors.joining("\n"));
    }

    private String mapRow(T item) {
        return columns.stream()
                .map(column -> column.apply(item))
                .collect(Collectors.joining(";"));
    }
}
