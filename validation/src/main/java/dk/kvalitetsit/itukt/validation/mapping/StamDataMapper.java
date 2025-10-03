package dk.kvalitetsit.itukt.validation.mapping;


import dk.kvalitetsit.itukt.validation.service.model.StamData;
import dk.kvalitetsit.itukt.validation.repository.StamDataEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamDataMapper {

    private static StamData merge(StamData x, StamData y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new StamData(x.drug(), clauses);
    }

    public Map<Long, StamData> map(List<StamDataEntity> entry) {
        return entry.stream()
                .distinct()
                .map(this::map)
                .collect(Collectors.toMap(
                        x -> x.drug().id(),
                        Function.identity(),
                        StamDataMapper::merge
                ));
    }

    private StamData map(StamDataEntity entity) {
        return new StamData(
                new StamData.Drug(entity.DrugId()),
                Set.of(new StamData.Clause(entity.Kode(), entity.Tekst()))
        );
    }
}
