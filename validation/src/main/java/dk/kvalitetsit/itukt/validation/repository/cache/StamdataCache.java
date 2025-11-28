package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.Repository;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamdataCache extends Cache<StamData, Long> {
    public StamdataCache(CacheConfiguration configuration, Repository<StamData> repository) {
        super(configuration, repository);
    }

    @Override
    StamData resolveConflict(StamData x, StamData y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new StamData(x.drug(), clauses);
    }

    @Override
    Long getIdentifier(StamData stamData) {
        return stamData.drug().id();
    }

}
