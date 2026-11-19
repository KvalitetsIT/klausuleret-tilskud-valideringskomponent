package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.Medication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;

public class ATCRepository {
    private final NamedParameterJdbcTemplate template;

    public ATCRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(Medication.ATC atc, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO ATC (ATC, ATCTekst, ValidFrom, ValidTo)
                VALUES (:atc, :tekst, :validFrom, :validTo)
                """;

        var params = Map.of(
                "atc", atc.code(),
                "tekst", "test",
                "validFrom", validFrom,
                "validTo", validTo
        );

        template.update(sql, params);
    }
}
