package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import org.openapitools.model.Indication;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IndicationServiceAdaptor implements StamdataCacheService<Indication> {
    private final StamdataCacheService<dk.kvalitetsit.itukt.common.model.Indication> indicationService;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Indication, Indication> mapper;

    public IndicationServiceAdaptor(StamdataCacheService<dk.kvalitetsit.itukt.common.model.Indication> indicationService, Mapper<dk.kvalitetsit.itukt.common.model.Indication, Indication> mapper) {
        this.indicationService = indicationService;
        this.mapper = mapper;
    }

    @Override
    public Set<Indication> getAll() {
        return indicationService.getAll().stream()
                .map(mapper::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Indication> get(String identifier) {
        return indicationService.get(identifier)
                .map(mapper::map);
    }
}
