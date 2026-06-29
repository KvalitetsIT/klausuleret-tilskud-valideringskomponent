package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.common.service.ClauseDrugCounter;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DrugClauseCacheImpl implements Cache<Long, DrugClause>, CacheLoader, ClauseDrugCounter {

    private final CacheConfiguration configuration;
    private final Repository<DrugClause> repository;
    private volatile Map<Long, DrugClause> entries = Map.of();
    private Map<String, Long> clauseDrugCount = Map.of();

    public DrugClauseCacheImpl(CacheConfiguration configuration, Repository<DrugClause> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        var response = repository.fetchAll();
        entries = toMap(response);
        clauseDrugCount = countDrugs(entries);
    }

    @Override
    public Optional<DrugClause> get(Long id) {
        return Optional.ofNullable(this.entries.get(id));
    }

    @Override
    public long getNumberOfDrugsForClause(String clauseCode) {
        return clauseDrugCount.getOrDefault(clauseCode, 0L);
    }

    private static DrugClause resolveConflict(DrugClause x, DrugClause y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new DrugClause(x.drug(), clauses);
    }

    private Map<Long, DrugClause> toMap(List<DrugClause> entries) {
        return entries.stream().collect(Collectors.toMap(
                entry -> entry.drug().id(),
                Function.identity(),
                DrugClauseCacheImpl::resolveConflict
        ));
    }

    private Map<String, Long> countDrugs(Map<Long, DrugClause> drugClauses) {
        return drugClauses.values().stream()
                .flatMap(drugClause -> drugClause.clauses().stream())
                .collect(Collectors.groupingBy(DrugClause.Clause::code, Collectors.counting()));
    }
}
