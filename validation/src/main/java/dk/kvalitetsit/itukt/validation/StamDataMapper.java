package dk.kvalitetsit.itukt.validation;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.StamData;
import dk.kvalitetsit.itukt.validation.repository.StamDataEntity;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamDataMapper implements Mapper<StamDataEntity, StamData> {

    private static StamData merge(StamData x, StamData y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new StamData(x.drug(), clauses);
    }

    @Override
    public List<StamData> map(List<StamDataEntity> entry) {
        return entry.stream()
                .distinct()
                .map(this::map)
                .collect(Collectors.toMap(
                        x -> x.drug().id(),
                        Function.identity(),
                        StamDataMapper::merge
                ))
                .values()
                .stream()
                .toList();

    }

    @Override
    public StamData map(StamDataEntity entity) {
        return new StamData(
                new StamData.Drug(entity.DrugId()),
                Set.of(new StamData.Clause(entity.Kode(), entity.Tekst()))
        );
    }
}
