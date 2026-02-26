package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.KlausuleringRepository;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.LaegemiddelRepository;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.PakningRepository;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.entity.Pakning;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugClauseViewRepositoryImpl;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrugClauseViewRepositoryImplIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private DrugClauseViewRepositoryImpl repository;
    private LaegemiddelRepository laegemiddelRepository;
    private PakningRepository pakningRepository;
    private KlausuleringRepository klausuleringRepository;
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        jdbcTemplate = new JdbcTemplate(stamDatabase.getDatasource());
        laegemiddelRepository = new LaegemiddelRepository(datasource);
        pakningRepository = new PakningRepository(datasource);
        klausuleringRepository = new KlausuleringRepository(datasource);
        this.repository = new DrugClauseViewRepositoryImpl(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM Laegemiddel");
        jdbcTemplate.execute("DELETE FROM Pakning");
        jdbcTemplate.execute("DELETE FROM Klausulering");
    }

    @Test
    void fetchAll_WhenLaegemiddelIsNotValidYet_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inTheFuture, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WhenLaegemiddelIsNotValidAnymore_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inThePast);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WhenPakningIsNotValidYet_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inTheFuture, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WhenPakningIsNotValidAnymore_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inThePast);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WhenKlausuleringIsNotValidYet_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inTheFuture, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WhenKlausuleringIsNotValidAnymore_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inThePast);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WithoutMatchingDrugId_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1111L);
        var pakning = new Pakning(1234L, "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering(pakning.klausuleringsKode(), "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WithoutMatchingKlausuleringsKode_ReturnsEmptyList() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning = new Pakning(laegemiddel.DrugId(), "TEST", 1L);
        var klausulering = new DrugClauseView.Klausulering("TEST2", "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertTrue(drugClauses.isEmpty());
    }

    @Test
    void fetchAll_WithMultiplePakning_ReturnsUniqueEntries() {
        var laegemiddel = new DrugClauseView.Laegemiddel(1234L);
        var pakning1 = new Pakning(laegemiddel.DrugId(), "TEST1", 1L);
        var pakning2 = new Pakning(laegemiddel.DrugId(), "TEST2", 2L);
        var pakning3 = new Pakning(laegemiddel.DrugId(), "TEST2", 3L);
        var klausulering1 = new DrugClauseView.Klausulering(pakning1.klausuleringsKode(), "test1");
        var klausulering2 = new DrugClauseView.Klausulering(pakning2.klausuleringsKode(), "test2");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning1, inThePast, inTheFuture);
        pakningRepository.insert(pakning2, inThePast, inTheFuture);
        pakningRepository.insert(pakning3, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering1, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering2, inThePast, inTheFuture);

        var drugClauses = repository.fetchAll();

        assertEquals(2, drugClauses.size());
        var clauseMap = drugClauses.stream()
                .collect(Collectors.toMap(clause -> clause.klausulering().Kode(), Function.identity()));
        assertTrue(clauseMap.containsKey(klausulering1.Kode()));
        assertTrue(clauseMap.containsKey(klausulering2.Kode()));
        assertEquals(laegemiddel, clauseMap.get(klausulering1.Kode()).laegemiddel());
        assertEquals(klausulering1, clauseMap.get(klausulering1.Kode()).klausulering());
        assertEquals(laegemiddel, clauseMap.get(klausulering2.Kode()).laegemiddel());
        assertEquals(klausulering2, clauseMap.get(klausulering2.Kode()).klausulering());
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }
}
