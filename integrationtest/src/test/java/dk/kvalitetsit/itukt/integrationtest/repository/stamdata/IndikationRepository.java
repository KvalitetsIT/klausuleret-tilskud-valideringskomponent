package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.Indication;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;

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

        var params = new MapSqlParameterSource()
                .addValue("indikationKode", indication.code())
                .addValue("tekst", indication.text())
                .addValue("validFrom", validFrom)
                .addValue("validTo", validTo);

        template.update(sql, params);
    }
}
