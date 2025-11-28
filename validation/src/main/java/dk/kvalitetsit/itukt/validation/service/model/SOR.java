package dk.kvalitetsit.itukt.validation.service.model;

public record SOR(String code, Type type) {
    enum Type {
        SOR,

    }
}
