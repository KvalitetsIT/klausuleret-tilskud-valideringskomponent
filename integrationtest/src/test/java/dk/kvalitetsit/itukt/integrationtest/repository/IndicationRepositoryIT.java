package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.IndikationRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.IndicationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndicationRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private IndicationRepository repository;
    private IndikationRepository indikationRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        indikationRepository = new IndikationRepository(datasource);
        repository = new IndicationRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @Test
    void fetchAll_WithNoIndicationInDB_ReturnsEmptyList() {
        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithBothValidAndInvalidIndicationInDB_ReturnsValidIndication() {
        var valid1 = new Indication(1);
        var valid2 = new Indication(2);
        var invalid1 = new Indication(3);
        var invalid2 = new Indication(4);
        indikationRepository.insert(valid1, inThePast, inTheFuture);
        indikationRepository.insert(valid2, inThePast, inTheFuture);
        indikationRepository.insert(invalid1, inThePast, inThePast);
        indikationRepository.insert(invalid2, inTheFuture, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(2, entries.size());
        assertTrue(entries.contains(valid1));
        assertTrue(entries.contains(valid2));
    }
}
