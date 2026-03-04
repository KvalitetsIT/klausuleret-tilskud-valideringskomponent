package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;

public class KlausuleringRepository {
    private final NamedParameterJdbcTemplate template;

    public KlausuleringRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(DrugClauseView.Klausulering klausulering, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Klausulering (Kode, Tekst, ValidFrom, ValidTo)
                VALUES (:kode, :tekst, :validFrom, :validTo)
                """;

        var params = new java.util.HashMap<String, Object>();
        params.put("kode", klausulering.Kode());
        params.put("tekst", klausulering.Tekst());
        params.put("validFrom", validFrom);
        params.put("validTo", validTo);

        template.update(sql, params);
    }
}
