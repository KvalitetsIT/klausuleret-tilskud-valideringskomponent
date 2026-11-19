package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.model.Indication;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class IndicationRepository implements Repository<Indication> {

    private final DataClassRowMapper<Indication> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public IndicationRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(Indication.class);
    }

    @Override
    public List<Indication> fetchAll() {
        try {
            String sql = """
                    SELECT DISTINCT IndikationKode as code
                    FROM Indikation
                    WHERE ValidTo > NOW() AND ValidFrom < NOW()
                    """;

            return template.query(sql, rowMapper);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all indication codes", e);
        }
    }
}
