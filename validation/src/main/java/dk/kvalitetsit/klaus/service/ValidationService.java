package dk.kvalitetsit.klaus.service;

public interface ValidationService<T> {
    boolean validate(T prescription);
}
