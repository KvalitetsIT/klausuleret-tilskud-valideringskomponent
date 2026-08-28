package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.model.Medication;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class MedicationFormRepository implements Repository<Medication.Form> {

    private final DataClassRowMapper<Medication.Form> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public MedicationFormRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(Medication.Form.class);
    }

    @Override
    public List<Medication.Form> fetchAll() {
        try {
            String sql = """
                    SELECT DISTINCT Kode as code
                    FROM Formbetegnelse
                    WHERE ValidTo > NOW() AND ValidFrom < NOW()
                    """;

            return template.query(sql, rowMapper);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all form codes", e);
        }
    }
}
