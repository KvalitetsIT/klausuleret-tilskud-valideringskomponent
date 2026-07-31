package dk.kvalitetsit.itukt.validation.stamdata.repository;

import java.util.List;

public interface Repository<T> {
    List<T> fetchAll();
}
