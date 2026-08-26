package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.model.DrugMedication;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class DrugMedicationFormRepository implements Repository<DrugMedication.Form> {

    private final DataClassRowMapper<DrugMedication.Form> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public DrugMedicationFormRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(DrugMedication.Form.class);
    }

    @Override
    public List<DrugMedication.Form> fetchAll() {
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
