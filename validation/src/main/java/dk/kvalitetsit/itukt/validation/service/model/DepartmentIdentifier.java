package dk.kvalitetsit.itukt.validation.service.model;

public record DepartmentIdentifier(String code, Type type) {
    public enum Type {
        SOR,
        SHAK
    }
}
