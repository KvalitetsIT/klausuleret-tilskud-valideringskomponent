package dk.kvalitetsit.itukt.validation.stamdata.repository.mapping;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.Set;

public class DrugClauseViewMapper implements Mapper<DrugClauseView, DrugClause> {

    @Override
    public DrugClause map(DrugClauseView entity) {
        return new DrugClause(
                new DrugClause.Drug(entity.laegemiddel().DrugId()),
                Set.of(new DrugClause.Clause(entity.klausulering().Kode(), entity.klausulering().Tekst()))
        );
    }
}
