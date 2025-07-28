package dk.kvalitetsit.klaus.repository.entity;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record ClauseEntity(
        String code,
        LocalDateTime validTo,
        long clausePID,
        String shortText,
        Timestamp lastReplicated,
        String text,
        LocalDateTime validFrom
) {}

