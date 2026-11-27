package dk.kvalitetsit.itukt.validation.service.mapping.error;

public interface ErrorMessageAssembler<T> {
    String assemble(T error);
}
