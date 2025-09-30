package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class StamDataRepositoryImpl implements StamDataRepository {

    private final DataClassRowMapper<StamDataEntity> clauseRowMapper;

    private final NamedParameterJdbcTemplate template;

    public StamDataRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        clauseRowMapper = DataClassRowMapper.newInstance(StamDataEntity.class);
    }

    @Override
    public List<StamDataEntity> findAll() throws ServiceException {
        try {
            String sql = """
                    SELECT d.DrugId, c.Kode, c.Tekst
                    FROM Laegemiddel d
                    JOIN Pakning p on p.DrugID = d.DrugID
                    JOIN Klausulering c ON c.kode = p.klausuleringskode
                    WHERE p.KlausuleringsKode IS NOT NULL
                    AND d.validfrom < NOW() AND d.validto > NOW()
                    AND p.validfrom < NOW() AND p.validto > NOW()
                    AND c.validfrom < NOW() AND c.validto > NOW()
                    """;

            return template.query(sql, clauseRowMapper);

        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all StamdataEntities", e);
        }
    }
}
