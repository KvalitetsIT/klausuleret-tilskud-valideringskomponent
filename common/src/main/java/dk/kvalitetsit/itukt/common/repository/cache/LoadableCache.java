package dk.kvalitetsit.itukt.common.repository.cache;

import java.util.List;

public interface LoadableCache<T> {
    void load(List<T> entries);
}
