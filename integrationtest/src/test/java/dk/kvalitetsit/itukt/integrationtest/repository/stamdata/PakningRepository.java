package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.entity.Pakning;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;

public class PakningRepository {
    private final NamedParameterJdbcTemplate template;

    public PakningRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(Pakning pakning, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Pakning (DrugId, KlausuleringsKode, ValidFrom, ValidTo, Varenummer)
                VALUES (:drugId, :klausuleringsKode, :validFrom, :validTo, :varenummer)
                """;

        var params = new java.util.HashMap<String, Object>();
        params.put("drugId", pakning.drugId());
        params.put("klausuleringsKode", pakning.klausuleringsKode());
        params.put("validFrom", validFrom);
        params.put("validTo", validTo);
        params.put("varenummer", pakning.Varenummer());

        template.update(sql, params);
    }
}
