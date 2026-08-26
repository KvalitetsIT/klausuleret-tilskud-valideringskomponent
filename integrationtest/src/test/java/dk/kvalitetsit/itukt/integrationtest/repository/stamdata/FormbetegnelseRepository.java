package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.DrugMedication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;

public class FormbetegnelseRepository {
    private final NamedParameterJdbcTemplate template;

    public FormbetegnelseRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(DrugMedication.Form form, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Formbetegnelse (Kode, Tekst, ValidFrom, ValidTo)
                VALUES (:kode, :tekst, :validFrom, :validTo)
                """;

        var params = new java.util.HashMap<String, Object>();
        params.put("kode", form.code());
        params.put("tekst", "test");
        params.put("validFrom", validFrom);
        params.put("validTo", validTo);

        template.update(sql, params);
    }
}
