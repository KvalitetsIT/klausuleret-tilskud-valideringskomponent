package dk.kvalitetsit.itukt.validation.repository.entity;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record ClauseEntity(
        String kode,
        LocalDateTime validto,
        Long klausuleringpid,
        String korttekst,
        Timestamp lastreplicated,
        String tekst,
        LocalDateTime validfrom
) {}

