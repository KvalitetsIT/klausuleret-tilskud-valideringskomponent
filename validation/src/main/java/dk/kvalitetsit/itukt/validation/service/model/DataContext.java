package dk.kvalitetsit.itukt.validation.service.model;

import java.util.List;
import java.util.Map;

public record DataContext(Map<String, List<String>> fields) {
    public List<String> get(String field) {
        return fields.getOrDefault(field, List.of());
    }
}
