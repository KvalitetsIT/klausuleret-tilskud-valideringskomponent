package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.model.Medication;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class MedicationATCRepository implements Repository<Medication.ATC> {

    private final DataClassRowMapper<Medication.ATC> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public MedicationATCRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(Medication.ATC.class);
    }

    @Override
    public List<Medication.ATC> fetchAll() {
        try {
            String sql = """
                    SELECT DISTINCT ATC as code
                    FROM ATC
                    WHERE ValidTo > NOW() AND ValidFrom < NOW()
                    """;

            return template.query(sql, rowMapper);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all atc codes", e);
        }
    }
}
