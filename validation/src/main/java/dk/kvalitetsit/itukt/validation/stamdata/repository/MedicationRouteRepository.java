package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.model.Medication;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class MedicationRouteRepository implements Repository<Medication.Route> {

    private final DataClassRowMapper<Medication.Route> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public MedicationRouteRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(Medication.Route.class);
    }

    @Override
    public List<Medication.Route> fetchAll() {
        try {
            String sql = """
                    SELECT DISTINCT AdministrationsvejKode as code
                    FROM Administrationsvej
                    WHERE ValidTo > NOW() AND ValidFrom < NOW()
                    """;

            return template.query(sql, rowMapper);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all route of administration codes", e);
        }
    }
}
