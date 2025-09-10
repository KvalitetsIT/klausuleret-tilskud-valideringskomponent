package dk.kvalitetsit.itukt.validation.service.model;

import java.util.Map;
import java.util.Optional;

public record DataContext(Map<String, Object> fields) {
    public Optional<Object> get(String field) {
        return Optional.ofNullable(fields.get(field));
    }
}
