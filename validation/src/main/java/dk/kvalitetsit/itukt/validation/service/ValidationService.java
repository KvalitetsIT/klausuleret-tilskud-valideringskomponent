package dk.kvalitetsit.itukt.validation.service;

public interface ValidationService<T, R> {
    R validate(T prescription);
}
