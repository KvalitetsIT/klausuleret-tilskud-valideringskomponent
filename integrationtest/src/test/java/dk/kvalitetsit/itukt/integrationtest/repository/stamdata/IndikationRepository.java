package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.Indication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;

public class IndikationRepository {
    private final NamedParameterJdbcTemplate template;

    public IndikationRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(Indication indication, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Indikation (IndikationKode, IndikationTekst, ValidFrom, ValidTo)
                VALUES (:indikationKode, :tekst, :validFrom, :validTo)
                """;

        var params = Map.of(
                "indikationKode", indication.code(),
                "tekst", "test",
                "validFrom", validFrom,
                "validTo", validTo
        );

        template.update(sql, params);
    }
}
