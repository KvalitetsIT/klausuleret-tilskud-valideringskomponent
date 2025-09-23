package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.configuration.CacheConfiguration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StamDataCache {

    private final Logger logger = LoggerFactory.getLogger(StamDataCache.class);
    private final CacheConfiguration configuration;

    private final StamDataRepository<StamData> concreteStamDataRepository;
    private Map<Long, StamData> drugIdToClauseMap = new HashMap<>();

    public StamDataCache(CacheConfiguration configuration, StamDataRepository<StamData> stamDataRepository) {
        this.configuration = configuration;
        this.concreteStamDataRepository = stamDataRepository;
    }

    @PostConstruct
    private void init() {
        this.drugIdToClauseMap = load();
    }

    @Scheduled(cron = "#{configuration.cron}")
    public void reload() {
        this.drugIdToClauseMap = load();
    }

    public Optional<StamData> getStamDataByDrugId(long drugId) {
        return Optional.ofNullable(drugIdToClauseMap.get(drugId));
    }

    private Map<Long, StamData> load() {
        var result = concreteStamDataRepository.findAll();
        logger.info("Loaded {} entries into cache", result.size());
        return result.stream().collect(Collectors.toMap(x -> x.drug().id(), x -> x, (x, y) -> x));
    }
}
