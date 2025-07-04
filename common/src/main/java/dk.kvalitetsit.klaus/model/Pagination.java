package dk.kvalitetsit.klaus.model;

import java.util.Optional;

public record Pagination(Optional<Integer> offset, Optional<Integer> limit) {
}
