package dk.kvalitetsit.itukt.validation.mapping;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.repository.StamDataEntity;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.Set;

public class StamDataMapper implements Mapper<StamDataEntity, StamData> {

    @Override
    public StamData map(StamDataEntity entity) {
        return new StamData(
                new StamData.Drug(entity.DrugId()),
                Set.of(new StamData.Clause(entity.Kode(), entity.Tekst()))
        );
    }
}
