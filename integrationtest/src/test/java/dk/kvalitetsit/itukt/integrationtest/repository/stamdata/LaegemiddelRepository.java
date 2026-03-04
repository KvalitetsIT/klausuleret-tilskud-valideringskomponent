package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;

public class LaegemiddelRepository {
    private final NamedParameterJdbcTemplate template;

    public LaegemiddelRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(DrugClauseView.Laegemiddel laegemiddel, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO Laegemiddel (DrugId, ValidFrom, ValidTo)
                VALUES (:drugId, :validFrom, :validTo)
                """;

        var params = new java.util.HashMap<String, Object>();
        params.put("drugId", laegemiddel.DrugId());
        params.put("validFrom", validFrom);
        params.put("validTo", validTo);

        template.update(sql, params);
    }
}
