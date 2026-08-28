package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.Medication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;

public class FormbetegnelseRepository {
    private final NamedParameterJdbcTemplate template;

    public FormbetegnelseRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(Medication.Form form, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Formbetegnelse (Kode, Tekst, ValidFrom, ValidTo)
                VALUES (:kode, :tekst, :validFrom, :validTo)
                """;

        var params = Map.of(
                "kode", form.code(),
                "tekst", "test",
                "validFrom", validFrom,
                "validTo", validTo
        );

        template.update(sql, params);
    }
}
