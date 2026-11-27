package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Indication;

public class IndicationMapper implements Mapper<Indication, org.openapitools.model.Indication> {
    @Override
    public org.openapitools.model.Indication map(Indication indication) {
        return new org.openapitools.model.Indication(
                indication.code(),
                indication.text()
        );
    }
}
