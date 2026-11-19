package dk.kvalitetsit.itukt.common.service;

import java.util.Optional;
import java.util.Set;

public interface StamdataCacheService<T> {
    Set<T> getAll();
    Optional<T> get(String identifier);
}
