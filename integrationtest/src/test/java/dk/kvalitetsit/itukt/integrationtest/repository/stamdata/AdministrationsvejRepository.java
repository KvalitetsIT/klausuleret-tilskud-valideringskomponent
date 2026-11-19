package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.Medication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;

public class AdministrationsvejRepository {
    private final NamedParameterJdbcTemplate template;

    public AdministrationsvejRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(Medication.Route route, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Administrationsvej (AdministrationsvejKode, AdministrationsvejTekst, ValidFrom, ValidTo)
                VALUES (:code, :text, :validFrom, :validTo)
                """;

        var params = Map.of(
                "code", route.code(),
                "text", "test",
                "validFrom", validFrom,
                "validTo", validTo
        );

        template.update(sql, params);
    }
}
