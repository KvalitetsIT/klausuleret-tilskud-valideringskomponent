package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepartmentCacheImpl implements Cache<Department.Identifier, Department>, CacheLoader {

    private final CacheConfiguration configuration;
    private final Repository<Department> repository;

    private volatile Map<Department.Identifier.SHAK, Department> shakEntries = Map.of();
    private volatile Map<Department.Identifier.SOR, Department> sorEntries = Map.of();

    public DepartmentCacheImpl(CacheConfiguration configuration, Repository<Department> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    private static Department resolveConflict(Department x, Department y) {
        Set<Department.Speciality> specialities = Stream.concat(x.specialities().stream(), y.specialities().stream()).collect(Collectors.toSet());
        return new Department(x.shak(), x.sor(), specialities);
    }

    private <T> Map<T, Department> toMap(List<Department> departments, Function<Department, Optional<T>> getId) {
        return departments.stream()
                .flatMap(dept -> getId.apply(dept).stream().map(id -> new AbstractMap.SimpleEntry<>(id, dept)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        DepartmentCacheImpl::resolveConflict
                ));
    }


    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        var response = repository.fetchAll();

        sorEntries = toMap(response, Department::sor);
        shakEntries = toMap(response, Department::shak);
    }

    @Override
    public Optional<Department> get(Department.Identifier code) {
        return switch (code) {
            case Department.Identifier.SHAK shak -> Optional.ofNullable(this.shakEntries.get(shak));
            case Department.Identifier.SOR sor -> Optional.ofNullable(this.sorEntries.get(sor));
        };
    }


}