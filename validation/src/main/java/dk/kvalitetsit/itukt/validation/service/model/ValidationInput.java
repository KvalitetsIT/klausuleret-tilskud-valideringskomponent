package dk.kvalitetsit.itukt.validation.service.model;

public record ValidationInput(
        int citizenAge,
        long drugId,
        String indicationCode) {
}
