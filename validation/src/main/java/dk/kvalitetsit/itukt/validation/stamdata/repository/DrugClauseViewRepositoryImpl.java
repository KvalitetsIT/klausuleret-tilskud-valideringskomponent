package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class DrugClauseViewRepositoryImpl implements DrugClauseViewRepository {

    private final static RowMapper<DrugClauseView> clauseRowMapper = (rs, rowNum) -> new DrugClauseView(
            new DrugClauseView.Laegemiddel(rs.getLong("d.DrugId")),
            new DrugClauseView.Klausulering(
                    rs.getString("k.Kode"),
                    rs.getString("k.Tekst")
            )
    );

    private final NamedParameterJdbcTemplate template;

    public DrugClauseViewRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<DrugClauseView> findAll() throws ServiceException {
        try {
            String sql = """
                    SELECT d.DrugId, k.Kode, k.Tekst
                    FROM Laegemiddel d
                    JOIN Pakning p on p.DrugID = d.DrugID
                    JOIN Klausulering k ON k.kode = p.klausuleringskode
                    WHERE p.KlausuleringsKode IS NOT NULL
                    AND d.validfrom < NOW() AND d.validto > NOW()
                    AND p.validfrom < NOW() AND p.validto > NOW()
                    AND k.validfrom < NOW() AND k.validto > NOW()
                    """;

            return template.query(sql, clauseRowMapper);

        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all StamdataEntities", e);
        }
    }
}
