package dk.kvalitetsit.itukt.validation.service;

public interface ValidationService<T> {
    boolean validate(T prescription);
}
