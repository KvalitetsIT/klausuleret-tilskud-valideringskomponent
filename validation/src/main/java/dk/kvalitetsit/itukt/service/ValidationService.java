package dk.kvalitetsit.itukt.service;

public interface ValidationService<T> {
    boolean validate(T prescription);
}
