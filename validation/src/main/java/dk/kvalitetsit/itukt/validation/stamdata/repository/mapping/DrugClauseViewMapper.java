package dk.kvalitetsit.itukt.validation.stamdata.repository.mapping;


import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DrugClauseViewMapper {

    private static DrugClause merge(DrugClause x, DrugClause y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new DrugClause(x.drug(), clauses);
    }

    public Map<Long, DrugClause> map(List<DrugClauseView> entry) {
        return entry.stream()
                .distinct()
                .map(this::map)
                .collect(Collectors.toMap(
                        x -> x.drug().id(),
                        Function.identity(),
                        DrugClauseViewMapper::merge
                ));
    }

    private DrugClause map(DrugClauseView entity) {
        return new DrugClause(
                new DrugClause.Drug(entity.laegemiddel().DrugId()),
                Set.of(new DrugClause.Clause(entity.klausulering().Kode(), entity.klausulering().Tekst()))
        );
    }
}
